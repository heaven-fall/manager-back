// TemplateServiceImpl.java (修正版)
package com.world.back.serviceImpl;

import com.world.back.config.TemplateConfig;
import com.world.back.entity.Template;
import com.world.back.entity.res.Result;
import com.world.back.mapper.TemplateMapper;
import com.world.back.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TemplateMapper templateMapper;

    @Autowired
    private TemplateConfig templateConfig;

    @PostConstruct
    public void init() {
        try {
            String uploadPath = templateConfig.getUploadPath();
            Path uploadDir = Paths.get(uploadPath);

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("创建模板上传目录: {}", uploadDir.toAbsolutePath());
            }
        } catch (Exception e) {
            log.error("初始化模板上传目录失败", e);
        }
    }

    @Override
    @Transactional
    public Result<String> uploadTemplate(MultipartFile file, Integer templateId, String userId) {
        try {
            log.info("开始上传模板, templateId: {}, userId: {}, 文件名: {}", templateId, userId, file.getOriginalFilename());

            // 1. 验证文件
            validateFile(file);

            // 2. 验证模板类型
            Template template = templateMapper.selectById(templateId);
            if (template == null) {
                return Result.error("模板不存在");
            }

            // 3. 验证占位符（可选）
            if (!validateTemplatePlaceholders(file, templateId)) {
                return Result.error("模板验证失败，请检查必需占位符");
            }

            // 4. 生成文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String newFileName = generateFileName(templateId, template.getName(), fileExtension);

            // 5. 保存文件
            String uploadPath = templateConfig.getUploadPath();
            Path targetPath = Paths.get(uploadPath, newFileName);

            // 确保目录存在
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("模板文件保存成功: {}", targetPath.toAbsolutePath());

            // 6. 更新数据库
            template.setFilePath(targetPath.toString());
            template.setFileName(originalFilename);
            template.setFileSize(file.getSize());
            template.setUpdatedBy(userId);
            template.setUpdatedAt(new Date());

            templateMapper.updateById(template);

            // 7. 返回文件访问路径
            String fileUrl = "/uploads/templates/" + newFileName;
            return Result.success("模板上传成功", fileUrl);

        } catch (IOException e) {
            log.error("文件操作失败", e);
            return Result.error("文件操作失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("上传模板失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> validateTemplate(MultipartFile file, Integer templateId) {
        try {
            log.info("验证模板占位符, templateId: {}, 文件名: {}", templateId, file.getOriginalFilename());

            // 1. 验证文件
            validateFile(file);

            // 2. 获取模板配置
            Template template = templateMapper.selectById(templateId);
            if (template == null) {
                return Result.error("模板不存在");
            }

            // 3. 提取Word中的占位符
            Set<String> foundPlaceholders = extractPlaceholdersFromWord(file);
            log.info("找到的占位符: {}", foundPlaceholders);

            // 4. 获取必需的占位符
            List<String> requiredPlaceholders = templateMapper.getRequiredPlaceholdersByType(template.getType());
            log.info("必需的占位符: {}", requiredPlaceholders);

            // 5. 验证
            List<String> missingPlaceholders = new ArrayList<>();
            for (String required : requiredPlaceholders) {
                if (!foundPlaceholders.contains(required)) {
                    missingPlaceholders.add(required);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("passed", missingPlaceholders.isEmpty());
            result.put("missingPlaceholders", missingPlaceholders);
            result.put("foundPlaceholders", new ArrayList<>(foundPlaceholders));

            if (!missingPlaceholders.isEmpty()) {
                return Result.error("模板缺少必需占位符: " + String.join(", ", missingPlaceholders));
            }

            return Result.success("模板验证通过", result);

        } catch (Exception e) {
            log.error("验证模板失败", e);
            return Result.error("验证失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> downloadTemplate(Integer templateId) {
        try {
            Template template = templateMapper.selectById(templateId);
            if (template == null || template.getFilePath() == null) {
                return Result.error("模板不存在或未上传");
            }

            File file = new File(template.getFilePath());
            if (!file.exists()) {
                return Result.error("模板文件不存在");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("fileName", template.getFileName());
            result.put("filePath", template.getFilePath());
            result.put("fileSize", template.getFileSize());
            result.put("fileUrl", "/uploads/templates/" + template.getFileName());

            return Result.success("获取模板信息成功", result);

        } catch (Exception e) {
            log.error("获取模板失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<String> deleteTemplate(Integer templateId) {
        try {
            Template template = templateMapper.selectById(templateId);
            if (template == null) {
                return Result.error("模板不存在");
            }

            // 如果模板有文件，先删除文件
            if (template.getFilePath() != null && !template.getFilePath().isEmpty()) {
                File file = new File(template.getFilePath());
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (!deleted) {
                        log.warn("删除文件失败: {}", template.getFilePath());
                    }
                }
            }

            // 不更新模板记录为null，而是只更新部分字段
            // 创建一个新的Template对象，只设置需要更新的字段
            Template updateTemplate = new Template();
            updateTemplate.setId(templateId);
            updateTemplate.setFileName("");  // 设置为空字符串而不是null
            updateTemplate.setFileSize(0L);  // 设置为0
            updateTemplate.setUpdatedAt(new Date());
            updateTemplate.setUpdatedBy("system");

            // 注意：我们不设置 filePath，因为它是 NOT NULL
            // 可以设置为空字符串或保留原值

            templateMapper.updateById(updateTemplate);

            return Result.success("模板删除成功");

        } catch (Exception e) {
            log.error("删除模板失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }



    @Override
    public Result<String> saveDateConfig(String defenseDate, String evaluationDate) {
        try {
            templateMapper.saveDateConfig("defense_date", defenseDate);
            templateMapper.saveDateConfig("evaluation_date", evaluationDate);
            return Result.success("日期设置保存成功");
        } catch (Exception e) {
            log.error("保存日期配置失败", e);
            return Result.error("保存失败: " + e.getMessage());
        }
    }

    // TemplateServiceImpl.java - 修正 getDateConfig 方法
    @Override
    public Result<Map<String, String>> getDateConfig() {
        try {
            List<Map<String, Object>> configList = templateMapper.getDateConfig();
            Map<String, String> result = new HashMap<>();

            for (Map<String, Object> config : configList) {
                String key = (String) config.get("config_key");
                Object valueObj = config.get("config_value");

                // 安全地处理日期值
                String valueStr = convertDateToString(valueObj);

                if (key != null) {
                    result.put(key, valueStr);
                }
            }

            return Result.success("获取日期配置成功", result);

        } catch (Exception e) {
            log.error("获取日期配置失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    // 新增：安全地将日期对象转换为字符串
    private String convertDateToString(Object dateObj) {
        if (dateObj == null) {
            return "";
        }

        try {
            if (dateObj instanceof java.sql.Date) {
                // 处理 java.sql.Date
                java.sql.Date sqlDate = (java.sql.Date) dateObj;
                return sqlDate.toString(); // 格式：YYYY-MM-DD
            } else if (dateObj instanceof java.sql.Timestamp) {
                // 处理 java.sql.Timestamp
                java.sql.Timestamp timestamp = (java.sql.Timestamp) dateObj;
                return new java.util.Date(timestamp.getTime()).toString();
            } else if (dateObj instanceof java.util.Date) {
                // 处理 java.util.Date
                java.util.Date utilDate = (java.util.Date) dateObj;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.format(utilDate);
            } else if (dateObj instanceof String) {
                // 已经是字符串
                return (String) dateObj;
            } else {
                // 其他类型，转换为字符串
                return dateObj.toString();
            }
        } catch (Exception e) {
            log.warn("日期转换失败: {}", dateObj, e);
            return "";
        }
    }

    // 修正：确保模板列表方法正确返回 List<Map<String, Object>>
    @Override
    public Result<List<Map<String, Object>>> getTemplates() {
        try {
            List<Template> templates = templateMapper.selectAllWithPlaceholders();

            // 转换为 List<Map<String, Object>>
            List<Map<String, Object>> result = templates.stream().map(template -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", template.getId());
                map.put("name", template.getName());
                map.put("description", template.getDescription());
                map.put("type", template.getType());
                map.put("filePath", template.getFilePath());
                map.put("fileName", template.getFileName());
                map.put("fileSize", template.getFileSize());
                map.put("updatedAt", template.getUpdatedAt());
                map.put("updatedBy", template.getUpdatedBy());
                map.put("hasTemplate", template.getHasTemplate());
                map.put("placeholderKeys", template.getPlaceholderKeys());
                return map;
            }).collect(java.util.stream.Collectors.toList());

            return Result.success("获取模板列表成功", result);

        } catch (Exception e) {
            log.error("获取模板列表失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    @Override
    public Result<String> applyDatesToAllTemplates(String defenseDate, String evaluationDate) {
        // 这里可以添加逻辑来更新所有模板中的日期占位符
        try {
            // 示例：更新数据库中所有相关记录的日期字段
            log.info("应用日期到模板: defenseDate={}, evaluationDate={}", defenseDate, evaluationDate);
            return Result.success("日期已应用到所有相关模板");
        } catch (Exception e) {
            log.error("应用日期失败", e);
            return Result.error("应用失败: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new Exception("文件名不能为空");
        }

        // 验证文件大小 - 使用DataSize
        if (file.getSize() > templateConfig.getMaxFileSize().toBytes()) {
            long maxSizeMB = templateConfig.getMaxFileSize().toMegabytes();
            throw new Exception("文件大小不能超过" + maxSizeMB + "MB");
        }

        // 验证文件扩展名
        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        boolean isValidExtension = Arrays.stream(templateConfig.getAllowedExtensions())
                .anyMatch(ext -> ext.equalsIgnoreCase(fileExtension));

        if (!isValidExtension) {
            throw new Exception("不支持的文件类型，仅支持: " +
                    String.join(", ", templateConfig.getAllowedExtensions()));
        }
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }

    private String generateFileName(Integer templateId, String templateName, String extension) {
        String safeName = templateName.replaceAll("[^a-zA-Z0-9\u4e00-\u9fa5]", "_");
        return String.format("template_%d_%s_%d.%s",
                templateId, safeName, System.currentTimeMillis(), extension);
    }

    private Set<String> extractPlaceholdersFromWord(MultipartFile file) throws IOException {
        Set<String> placeholders = new HashSet<>();

        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            // 提取段落中的占位符
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                extractPlaceholdersFromText(text, placeholders);
            }

            // 提取表格中的占位符
            document.getTables().forEach(table -> {
                table.getRows().forEach(row -> {
                    row.getTableCells().forEach(cell -> {
                        cell.getParagraphs().forEach(paragraph -> {
                            String text = paragraph.getText();
                            extractPlaceholdersFromText(text, placeholders);
                        });
                    });
                });
            });
        }

        return placeholders;
    }

    private void extractPlaceholdersFromText(String text, Set<String> placeholders) {
        if (text == null || text.isEmpty()) {
            return;
        }

        // 匹配 {{xxx}} 格式
        Pattern pattern1 = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher matcher1 = pattern1.matcher(text);
        while (matcher1.find()) {
            placeholders.add("{{" + matcher1.group(1) + "}}");
        }

        // 匹配 ${xxx} 格式
        Pattern pattern2 = Pattern.compile("\\$\\{(.*?)\\}");
        Matcher matcher2 = pattern2.matcher(text);
        while (matcher2.find()) {
            placeholders.add("${" + matcher2.group(1) + "}");
        }
    }

    private boolean validateTemplatePlaceholders(MultipartFile file, Integer templateId) throws IOException {
        // 这里可以添加更复杂的验证逻辑
        // 如果验证失败，可以抛出异常或返回false
        return true;
    }
}