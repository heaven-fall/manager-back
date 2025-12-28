// TemplateService.java - 修正接口定义
package com.world.back.service;

import com.world.back.entity.res.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface TemplateService {
    Result<String> uploadTemplate(MultipartFile file, Integer templateId, String userId);
    Result<Map<String, Object>> validateTemplate(MultipartFile file, Integer templateId);
    Result<Map<String, Object>> downloadTemplate(Integer templateId);
    Result<String> deleteTemplate(Integer templateId);
    Result<List<Map<String, Object>>> getTemplates();  // 修改返回类型
    Result<String> saveDateConfig(String defenseDate, String evaluationDate);
    Result<Map<String, String>> getDateConfig();
    Result<String> applyDatesToAllTemplates(String defenseDate, String evaluationDate);
}