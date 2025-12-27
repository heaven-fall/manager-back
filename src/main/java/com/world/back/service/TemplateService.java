package com.world.back.service;

import com.world.back.entity.Template;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 模板管理服务接口
 */
public interface TemplateService {

    /**
     * 获取所有模板列表
     * @return 模板列表
     */
    List<Template> getTemplateList();

    /**
     * 上传模板文件
     * @param file Word文件
     * @param templateId 模板ID
     * @param userId 用户ID
     * @return 上传结果
     */
    Map<String, Object> uploadTemplate(MultipartFile file, Integer templateId, String userId);

    /**
     * 下载模板文件
     * @param templateId 模板ID
     * @param response HTTP响应
     */
    void downloadTemplate(Integer templateId, HttpServletResponse response);

    /**
     * 删除模板文件
     * @param templateId 模板ID
     * @return 是否删除成功
     */
    boolean deleteTemplate(Integer templateId);

    /**
     * 验证模板占位符
     * @param file Word文件
     * @param templateId 模板ID
     * @return 验证结果
     */
    Map<String, Object> validateTemplatePlaceholders(MultipartFile file, Integer templateId);

    /**
     * 获取模板信息
     * @param templateId 模板ID
     * @return 模板信息
     */
    Template getTemplateInfo(Integer templateId);

    /**
     * 检查模板是否存在
     * @param templateId 模板ID
     * @return 是否存在
     */
    boolean existsTemplate(Integer templateId);

    /**
     * 获取模板统计信息
     * @return 统计信息
     */
    Map<String, Object> getTemplateStats();
}