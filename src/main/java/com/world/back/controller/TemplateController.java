// TemplateController.java - 修正模板列表方法
package com.world.back.controller;

import com.world.back.entity.res.Result;
import com.world.back.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/templates")
@CrossOrigin
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    // 上传模板
    @PostMapping("/upload")
    public Result<String> uploadTemplate(
            @RequestParam("file") MultipartFile file,
            @RequestParam Integer templateId,
            @RequestParam String userId) {
        return templateService.uploadTemplate(file, templateId, userId);
    }

    // 验证模板
    @PostMapping("/validate")
    public Result<Map<String, Object>> validateTemplate(
            @RequestParam("file") MultipartFile file,
            @RequestParam Integer templateId) {
        return templateService.validateTemplate(file, templateId);
    }

    // 下载模板
    @GetMapping("/download/{id}")
    public void downloadTemplate(@PathVariable Integer id,
                                 HttpServletResponse response) {
        try {
            Result<Map<String, Object>> result = templateService.downloadTemplate(id);
            if (result.getCode() != 200) {
                response.setStatus(404);
                response.getWriter().write(result.getMessage());
                return;
            }

            Map<String, Object> data = result.getData();
            String filePath = (String) data.get("filePath");
            String fileName = (String) data.get("fileName");

            if (filePath == null || fileName == null) {
                response.setStatus(404);
                response.getWriter().write("文件不存在");
                return;
            }

            File file = new File(filePath);
            if (!file.exists()) {
                response.setStatus(404);
                response.getWriter().write("文件不存在");
                return;
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setContentLength((int) file.length());

            // 将文件流写入response
            Files.copy(Paths.get(filePath), response.getOutputStream());
            response.flushBuffer();

        } catch (Exception e) {
            try {
                response.setStatus(500);
                response.getWriter().write("下载失败: " + e.getMessage());
            } catch (Exception ex) {
                // 忽略
            }
        }
    }

    // 删除模板
    @DeleteMapping("/{id}")
    public Result<String> deleteTemplate(@PathVariable Integer id) {
        return templateService.deleteTemplate(id);
    }

    // 获取所有模板 - 修正返回类型
    @GetMapping
    public Result<List<Map<String, Object>>> getTemplates() {
        return templateService.getTemplates();
    }

    // 保存日期配置
    @PostMapping("/date-config/save")
    public Result<String> saveDateConfig(
            @RequestBody Map<String, String> params) {
        String defenseDate = params.get("defenseDate");
        String evaluationDate = params.get("evaluationDate");
        return templateService.saveDateConfig(defenseDate, evaluationDate);
    }

    // 获取日期配置
    @GetMapping("/date-config")
    public Result<Map<String, String>> getDateConfig() {
        return templateService.getDateConfig();
    }

    // 应用日期到所有模板
    @PostMapping("/apply-dates")
    public Result<String> applyDatesToAllTemplates(
            @RequestBody Map<String, String> params) {
        String defenseDate = params.get("defenseDate");
        String evaluationDate = params.get("evaluationDate");
        return templateService.applyDatesToAllTemplates(defenseDate, evaluationDate);
    }
}