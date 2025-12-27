package com.world.back.serviceImpl;

import com.world.back.entity.res.Result;
import com.world.back.entity.user.Admin;
import com.world.back.entity.user.BaseUser;
import com.world.back.entity.user.InstituteAdmin;
import com.world.back.mapper.UserMapper;
import com.world.back.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService
{
  @Autowired
  private UserMapper userMapper;

  @Value("${signature.upload.path}")
  private String uploadPath;

  @Value("${signature.allowed-extensions}")
  private String allowedExtensions;

  private static final DateTimeFormatter DATE_FORMATTER =
          DateTimeFormatter.ofPattern("yyyyMMdd");

  @Override
  public Integer getAdminCount()
  {
    return userMapper.getAdminCount();
  }

  @Override
  public Boolean createAdmin(InstituteAdmin admin)
  {
    userMapper.createAdmin(admin.getId(), admin.getRealName(), admin.getRole(), admin.getPwd());
    userMapper.createUserInstRel(admin.getId(), admin.getInstId());
    return true;
  }

  @Override
  public Boolean updateAdmin(String realName, String username, String phone, String email)
  {
    userMapper.updateAdmin(realName, username, phone, email);
    return true;
  }

  @Override
  public List<Admin> getAllAdmins()
  {
    return userMapper.getAllAdmins();
  }

  @Override
  public boolean changePassword(String userId, String oldPassword, String newPassword) {
    // 1. 验证用户存在
    BaseUser user = userMapper.getUserById(userId);
    if (user == null) {
      return false;
    }

    // 2. 验证原密码是否正确
    if (!user.getPwd().equals(oldPassword)) {
      return false;
    }

    // 3. 更新密码
    int rows = userMapper.updatePassword(userId, newPassword);
    return rows > 0;
  }

  @Override
  public String getNameById(String id)
  {
    BaseUser user = userMapper.getUserById(id);
    if (user != null)
    {
      return user.getRealName();
    }
    return "查无此人";
  }

  @Override
  public Result<String> getCurrentSignature(String userId) {
    try {
      if (StringUtils.isEmpty(userId)) {
        return Result.error("用户ID不能为空");
      }

      BaseUser user = userMapper.getUserById(userId);
      if (user == null) {
        return Result.error("用户不存在");
      }

      if (StringUtils.isEmpty(user.getSignaturePath())) {
        return Result.success("用户未上传签名", null);
      }

      String signaturePath = user.getSignaturePath();

      if (signaturePath.startsWith("/uploads/signatures/")) {
        signaturePath = "/api" + signaturePath;
      } else if (!signaturePath.startsWith("/api/uploads/signatures/")) {
        // 添加前缀
        signaturePath = "/api/uploads/signatures/" + signaturePath;
      }

      log.info("返回给前端的签名路径: {}", signaturePath);
      // 返回签名文件路径信息
      return Result.success("获取成功", signaturePath);
    } catch (Exception e) {
      log.error("获取用户签名失败，userId: {}", userId, e);
      return Result.error("获取签名失败");
    }
  }

  @Override
  public Result<String> uploadSignature(MultipartFile file, String userId) {
    log.info("开始上传签名，用户ID: {}, 文件名: {}, 文件大小: {} bytes",
            userId, file.getOriginalFilename(), file.getSize());

    try {
      // 参数校验
      if (StringUtils.isEmpty(userId)) {
        return Result.error("用户ID不能为空");
      }

      if (file.isEmpty()) {
        return Result.error("请选择要上传的文件");
      }

      // 校验用户是否存在
      BaseUser user = userMapper.getUserById(userId);
      if (user == null) {
        return Result.error("用户不存在");
      }

      // 文件类型校验
      String originalFilename = file.getOriginalFilename();
      String fileExtension = StringUtils.getFilenameExtension(originalFilename);

      if (!StringUtils.hasText(fileExtension) ||
              !Arrays.asList(allowedExtensions.split(","))
                      .contains(fileExtension.toLowerCase())) {
        return Result.error("文件类型不支持，仅支持：" + allowedExtensions);
      }

      // 文件大小校验
      if (file.getSize() > 3 * 1024 * 1024) { // 3MB
        return Result.error("文件大小不能超过3MB");
      }

      // 构建上传目录路径
      String baseUploadPath = getUploadBasePath();
      log.info("基础上传路径: {}", baseUploadPath);

      // 创建日期目录
      String dateDir = DATE_FORMATTER.format(LocalDateTime.now());
      String uploadDirPath = baseUploadPath + dateDir + File.separator;

      Path uploadDir = Paths.get(uploadDirPath);
      log.info("上传目录: {}", uploadDir.toAbsolutePath());

      // 创建目录
      if (!Files.exists(uploadDir)) {
        Files.createDirectories(uploadDir);
        log.info("创建上传目录: {}", uploadDir.toAbsolutePath());
      }

      // 生成唯一文件名
      String newFilename = UUID.randomUUID() + "_" + userId + "." + fileExtension;
      log.info("生成文件名: {}", newFilename);

      // 保存文件
      Path filePath = uploadDir.resolve(newFilename);
      log.info("文件保存路径: {}", filePath.toAbsolutePath());

      // 保存文件
      file.transferTo(filePath.toFile());
      log.info("文件保存成功，大小: {} bytes", filePath.toFile().length());

      String relativePath = "/api/uploads/signatures/" + dateDir + "/" + newFilename;
      log.info("返回给前端的相对路径: {}", relativePath);

      // 删除旧签名文件（如果存在）
      deleteOldSignature(user.getSignaturePath());

      // 更新数据库 - 存储相对路径（不包含 /api 前缀）
      String dbPath = "/uploads/signatures/" + dateDir + "/" + newFilename;
      int updateCount = userMapper.updateSignaturePath(userId, dbPath);
      if (updateCount <= 0) {
        // 删除刚刚上传的文件
        Files.deleteIfExists(filePath);
        return Result.error("更新用户签名信息失败");
      }

      log.info("用户{}上传签名成功，文件路径：{}", userId, relativePath);
      return Result.success("签名上传成功", relativePath);

    } catch (IOException e) {
      log.error("保存签名文件失败，userId: {}, 错误: {}", userId, e.getMessage(), e);
      return Result.error("文件保存失败: " + e.getMessage());
    } catch (Exception e) {
      log.error("上传签名失败，userId: {}, 错误: {}", userId, e.getMessage(), e);
      return Result.error("上传失败: " + e.getMessage());
    }
  }

  private String getUploadBasePath() {
    // 获取项目根目录
    String projectRoot = System.getProperty("user.dir");

    // 根据配置的路径类型处理
    if (uploadPath.startsWith("./")) {
      // 相对路径：./uploads/signatures/
      return projectRoot + File.separator + uploadPath.substring(2);
    } else if (uploadPath.startsWith("/") || uploadPath.contains(":")) {
      // 绝对路径：/path/to/uploads/ 或 D:/uploads/
      return uploadPath;
    } else {
      // 相对路径但不以./开头
      return projectRoot + File.separator + uploadPath;
    }
  }

  //将相对路径转换为绝对路径
  private String convertRelativeToAbsolute(String relativePath) {
    if (!StringUtils.hasText(relativePath)) {
      return null;
    }

    try {
      // 去掉开头的斜杠
      String cleanPath = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;

      // 获取基础路径
      String basePath = getUploadBasePath();

      // 构建绝对路径
      String absolutePath = basePath + cleanPath;

      // 标准化路径
      Path normalizedPath = Paths.get(absolutePath).normalize();

      return normalizedPath.toString();
    } catch (Exception e) {
      log.error("转换路径失败: {}", relativePath, e);
      return null;
    }
  }

  // 修复删除旧签名方法
  private void deleteOldSignature(String oldSignaturePath) {
    if (!StringUtils.hasText(oldSignaturePath)) {
      return;
    }

    try {
      // 如果是相对路径，转换为绝对路径
      String oldFilePath;
      if (oldSignaturePath.startsWith("/api/uploads/signatures/")) {
        // 去掉 /api 前缀
        oldFilePath = uploadPath + oldSignaturePath.substring("/api/uploads/signatures/".length());
      } else if (oldSignaturePath.startsWith("/uploads/signatures/")) {
        oldFilePath = uploadPath + oldSignaturePath.substring("/uploads/signatures/".length());
      } else {
        oldFilePath = oldSignaturePath;
      }

      // 清理路径
      oldFilePath = oldFilePath.replace("./", "").replace(".\\", "");
      Path oldPath = Paths.get(oldFilePath).normalize();

      if (Files.exists(oldPath)) {
        Files.delete(oldPath);
        log.info("删除旧签名文件：{}", oldFilePath);
      }
    } catch (Exception e) {
      log.warn("删除旧签名文件失败：{}", oldSignaturePath, e);
    }
  }
  
}
