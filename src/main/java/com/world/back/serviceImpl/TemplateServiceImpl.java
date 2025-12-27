package com.world.back.serviceImpl;

import com.world.back.entity.Template;
import com.world.back.mapper.PlaceholderConfigMapper;
import com.world.back.mapper.TemplateMapper;
import com.world.back.service.TemplateService;
import com.world.back.utils.FileUtil;
import com.world.back.utils.WordParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final TemplateMapper templateMapper;
    private final PlaceholderConfigMapper placeholderConfigMapper;

    @Value("${file.upload.dir:uploads/templates}")
    private String uploadDir;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<Template> getTemplateList() {
        log.info("获取所有模板列表");
        List<Template> templates = templateMapper.findAll();

        templates.forEach(template -> {
            // 判断是否有模板文件
            boolean hasTemplate = template.getFilePath() != null &&
                    !template.getFilePath().isEmpty() &&
                    new File(template.getFilePath()).exists();
            template.setHasTemplate(hasTemplate);

            // 获取必需占位符
            List<String> placeholders = placeholderConfigMapper
                    .findRequiredPlaceholdersByType(template.getType());
            template.setRequiredPlaceholders(placeholders.toArray(new String[0]));

            // 格式化更新时间
            if (template.getUpdatedAt() != null) {
                template.setUpdatedAtFormatted(dateFormat.format(template.getUpdatedAt()));
            }
        });

        return templates;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> uploadTemplate(MultipartFile file, Integer templateId, String userId) {
        log.info("上传模板，templateId: {}, userId: {}, fileName: {}",
                templateId, userId, file.getOriginalFilename());

        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 验证文件
            validateFile(file);

            // 2. 获取模板信息
            Template template = templateMapper.findById(templateId);
            if (template == null) {
                log.error("模板不存在，templateId: {}", templateId);
                result.put("code", 404);
                result.put("message", "模板不存在");
                return result;
            }

            // 3. 验证占位符
            List<String> requiredPlaceholders = placeholderConfigMapper
                    .findRequiredPlaceholdersByType(template.getType());

            List<String> filePlaceholders = WordParser.extractPlaceholders(file);

            List<String> missingPlaceholders = requiredPlaceholders.stream()
                    .filter(placeholder -> !filePlaceholders.contains(placeholder))
                    .collect(Collectors.toList());

            if (!missingPlaceholders.isEmpty()) {
                log.warn("模板缺少必需占位符，templateId: {}, missing: {}",
                        templateId, missingPlaceholders);
                result.put("code", 400);
                result.put("message", "模板缺少必需占位符");
                result.put("data", Map.of("missingPlaceholders", missingPlaceholders));
                return result;
            }

            // 4. 保存文件
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = System.currentTimeMillis() + "_" + UUID.randomUUID() + fileExtension;
            Path filePath = Paths.get(uploadDir, newFilename);

            // 创建目录
            Files.createDirectories(filePath.getParent());

            // 保存文件
            file.transferTo(filePath.toFile());
            log.info("文件保存成功，路径: {}", filePath);

            // 5. 删除旧文件（如果存在）
            if (template.getFilePath() != null && !template.getFilePath().isEmpty()) {
                File oldFile = new File(template.getFilePath());
                if (oldFile.exists() && oldFile.isFile()) {
                    boolean deleted = oldFile.delete();
                    if (deleted) {
                        log.info("删除旧文件成功: {}", template.getFilePath());
                    } else {
                        log.warn("删除旧文件失败: {}", template.getFilePath());
                    }
                }
            }

            // 6. 更新数据库
            template.setFilePath(filePath.toString());
            template.setFileName(originalFilename);
            template.setFileSize(file.getSize());
            template.setUpdatedBy(userId);

            int updateCount = templateMapper.updateFileInfo(template);
            if (updateCount == 0) {
                // 更新失败，删除已上传的文件
                Files.deleteIfExists(filePath);
                log.error("更新数据库失败，删除已上传的文件");
                result.put("code", 500);
                result.put("message", "更新数据库失败");
                return result;
            }

            log.info("模板上传成功，templateId: {}", templateId);

            // 7. 返回结果
            result.put("code", 200);
            result.put("message", "上传成功");
            result.put("data", Map.of(
                    "fileSize", FileUtil.formatFileSize(file.getSize()),
                    "updatedAt", new Date(),
                    "updatedBy", userId,
                    "fileName", originalFilename,
                    "templateName", template.getName()
            ));

        } catch (Exception e) {
            log.error("上传模板失败", e);
            result.put("code", 500);
            result.put("message", "上传失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    public void downloadTemplate(Integer templateId, HttpServletResponse response) {
        log.info("下载模板，templateId: {}", templateId);

        try {
            Template template = templateMapper.findById(templateId);
            if (template == null || template.getFilePath() == null || template.getFilePath().isEmpty()) {
                sendErrorResponse(response, 404, "模板不存在");
                return;
            }

            File file = new File(template.getFilePath());
            if (!file.exists() || !file.isFile()) {
                sendErrorResponse(response, 404, "文件不存在");
                return;
            }

            // 设置响应头
            String filename = template.getFileName() != null ?
                    template.getFileName() : template.getName() + getFileExtension(file.getName());

            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + java.net.URLEncoder.encode(filename, "UTF-8") + "\"");
            response.setContentLengthLong(file.length());
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            // 写入文件流
            try (InputStream is = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }

            log.info("模板下载成功，templateId: {}, fileName: {}", templateId, filename);

        } catch (Exception e) {
            log.error("下载模板失败", e);
            sendErrorResponse(response, 500, "下载失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTemplate(Integer templateId) {
        log.info("删除模板，templateId: {}", templateId);

        try {
            Template template = templateMapper.findById(templateId);
            if (template == null) {
                log.error("模板不存在，无法删除，templateId: {}", templateId);
                return false;
            }

            // 删除物理文件
            if (template.getFilePath() != null && !template.getFilePath().isEmpty()) {
                File file = new File(template.getFilePath());
                if (file.exists() && file.isFile()) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        log.info("删除物理文件成功: {}", template.getFilePath());
                    } else {
                        log.warn("删除物理文件失败: {}", template.getFilePath());
                    }
                }
            }

            // 清空数据库记录
            int updateCount = templateMapper.clearFileInfo(templateId);
            if (updateCount > 0) {
                log.info("模板删除成功，templateId: {}", templateId);
                return true;
            } else {
                log.error("更新数据库失败，templateId: {}", templateId);
                return false;
            }

        } catch (Exception e) {
            log.error("删除模板失败", e);
            throw new RuntimeException("删除模板失败", e);
        }
    }

    @Override
    public Map<String, Object> validateTemplatePlaceholders(MultipartFile file, Integer templateId) {
        log.info("验证模板占位符，templateId: {}", templateId);

        Map<String, Object> result = new HashMap<>();

        try {
            Template template = templateMapper.findById(templateId);
            if (template == null) {
                result.put("code", 404);
                result.put("message", "模板不存在");
                return result;
            }

            // 验证文件
            validateFile(file);

            List<String> requiredPlaceholders = placeholderConfigMapper
                    .findRequiredPlaceholdersByType(template.getType());

            List<String> filePlaceholders = WordParser.extractPlaceholders(file);

            List<String> missingPlaceholders = requiredPlaceholders.stream()
                    .filter(placeholder -> !filePlaceholders.contains(placeholder))
                    .collect(Collectors.toList());

            List<String> extraPlaceholders = filePlaceholders.stream()
                    .filter(placeholder -> !requiredPlaceholders.contains(placeholder))
                    .collect(Collectors.toList());

            boolean passed = missingPlaceholders.isEmpty();

            log.info("占位符验证结果，templateId: {}, passed: {}, missing: {}, extra: {}",
                    templateId, passed, missingPlaceholders.size(), extraPlaceholders.size());

            result.put("code", 200);
            result.put("data", Map.of(
                    "passed", passed,
                    "missingPlaceholders", missingPlaceholders,
                    "extraPlaceholders", extraPlaceholders,
                    "totalRequired", requiredPlaceholders.size(),
                    "found", filePlaceholders.size(),
                    "templateName", template.getName()
            ));

        } catch (Exception e) {
            log.error("验证占位符失败", e);
            result.put("code", 500);
            result.put("message", "验证失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Template getTemplateInfo(Integer templateId) {
        log.info("获取模板信息，templateId: {}", templateId);
        Template template = templateMapper.findById(templateId);

        if (template != null) {
            template.setHasTemplate(template.getFilePath() != null &&
                    !template.getFilePath().isEmpty() &&
                    new File(template.getFilePath()).exists());

            List<String> placeholders = placeholderConfigMapper
                    .findRequiredPlaceholdersByType(template.getType());
            template.setRequiredPlaceholders(placeholders.toArray(new String[0]));
        }

        return template;
    }

    @Override
    public boolean existsTemplate(Integer templateId) {
        return templateMapper.findById(templateId) != null;
    }

    @Override
    public Map<String, Object> getTemplateStats() {
        List<Template> templates = templateMapper.findAll();

        long totalTemplates = templates.size();
        long uploadedTemplates = templates.stream()
                .filter(t -> t.getFilePath() != null && !t.getFilePath().isEmpty())
                .count();

        long totalFileSize = templates.stream()
                .mapToLong(t -> t.getFileSize() != null ? t.getFileSize() : 0)
                .sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTemplates", totalTemplates);
        stats.put("uploadedTemplates", uploadedTemplates);
        stats.put("notUploadedTemplates", totalTemplates - uploadedTemplates);
        stats.put("uploadedPercentage", totalTemplates > 0 ?
                String.format("%.1f%%", (uploadedTemplates * 100.0 / totalTemplates)) : "0%");
        stats.put("totalFileSize", FileUtil.formatFileSize(totalFileSize));
        stats.put("lastUpdated", getLastUpdatedTime(templates));

        return stats;
    }

    private void validateFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 文件大小验证（10MB）
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("文件大小不能超过10MB");
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 文件类型验证
        String lowerCaseFilename = filename.toLowerCase();
        if (!lowerCaseFilename.endsWith(".doc") && !lowerCaseFilename.endsWith(".docx")) {
            throw new IllegalArgumentException("只支持上传Word文档 (.doc/.docx)");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) {
        try {
            response.setStatus(status);
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");

            String jsonResponse = String.format(
                    "{\"code\":%d,\"message\":\"%s\",\"timestamp\":\"%s\"}",
                    status, message, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
            );

            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("发送错误响应失败", e);
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex) : ".docx";
    }

    private String getLastUpdatedTime(List<Template> templates) {
        return templates.stream()
                .filter(t -> t.getUpdatedAt() != null)
                .max(Comparator.comparing(Template::getUpdatedAt))
                .map(t -> dateFormat.format(t.getUpdatedAt()))
                .orElse("暂无");
    }
}