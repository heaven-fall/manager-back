package com.world.back.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class WordParser {

    public static List<String> extractPlaceholders(MultipartFile file) throws Exception {
        List<String> placeholders = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             XWPFDocument document = new XWPFDocument(is);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            String text = extractor.getText();
            log.debug("提取Word文本，长度: {}", text.length());

            // 匹配 {{placeholder}} 格式
            Pattern pattern1 = Pattern.compile("\\{\\{([^}]+)\\}\\}");
            Matcher matcher1 = pattern1.matcher(text);
            while (matcher1.find()) {
                String placeholder = "{{" + matcher1.group(1).trim() + "}}";
                placeholders.add(placeholder);
                log.debug("找到占位符(格式1): {}", placeholder);
            }

            // 匹配 ${placeholder} 格式
            Pattern pattern2 = Pattern.compile("\\$\\{([^}]+)\\}");
            Matcher matcher2 = pattern2.matcher(text);
            while (matcher2.find()) {
                String placeholder = "{{" + matcher2.group(1).trim() + "}}"; // 统一格式
                placeholders.add(placeholder);
                log.debug("找到占位符(格式2，转换后): {}", placeholder);
            }

            // 去重
            List<String> uniquePlaceholders = new ArrayList<>(new HashSet<>(placeholders));
            log.info("从文件中提取占位符，总数: {}，去重后: {}", placeholders.size(), uniquePlaceholders.size());

            return uniquePlaceholders;
        } catch (Exception e) {
            log.error("解析Word文档失败", e);
            throw new RuntimeException("解析Word文档失败: " + e.getMessage());
        }
    }

    public static boolean containsAllPlaceholders(MultipartFile file, List<String> requiredPlaceholders) throws Exception {
        List<String> foundPlaceholders = extractPlaceholders(file);

        for (String required : requiredPlaceholders) {
            if (!foundPlaceholders.contains(required)) {
                log.warn("缺少必需占位符: {}", required);
                return false;
            }
        }

        return true;
    }

    public static Map<String, Object> analyzePlaceholders(MultipartFile file, List<String> requiredPlaceholders) throws Exception {
        List<String> foundPlaceholders = extractPlaceholders(file);

        Map<String, Object> result = new HashMap<>();
        List<String> missing = new ArrayList<>();
        List<String> extra = new ArrayList<>();

        for (String required : requiredPlaceholders) {
            if (!foundPlaceholders.contains(required)) {
                missing.add(required);
            }
        }

        for (String found : foundPlaceholders) {
            if (!requiredPlaceholders.contains(found)) {
                extra.add(found);
            }
        }

        result.put("foundPlaceholders", foundPlaceholders);
        result.put("missingPlaceholders", missing);
        result.put("extraPlaceholders", extra);
        result.put("hasAllRequired", missing.isEmpty());
        result.put("foundCount", foundPlaceholders.size());
        result.put("requiredCount", requiredPlaceholders.size());

        return result;
    }
}