package com.world.back.controller;

import com.world.back.entity.Template;
import com.world.back.service.TemplateService;
import com.world.back.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/templates")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> getTemplates(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 验证token
           // String userId = AuthUtil.getUserIdFromToken(request);

            List<Template> templates = templateService.getTemplateList();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", templates);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取模板列表失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadTemplate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("templateId") Integer templateId,
            HttpServletRequest request) {

        try {
            String userId = AuthUtil.getUserIdFromToken(request);
            Map<String, Object> result = templateService.uploadTemplate(file, templateId, userId);

            int code = (int) result.get("code");
            if (code == 200) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(code).body(result);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/download/{id}")
    public void downloadTemplate(@PathVariable Integer id, HttpServletResponse response) {
        templateService.downloadTemplate(id, response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTemplate(
            @PathVariable Integer id,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        try {
            AuthUtil.getUserIdFromToken(request); // 验证token

            boolean success = templateService.deleteTemplate(id);
            if (success) {
                response.put("code", 200);
                response.put("message", "删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "模板不存在");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateTemplate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("templateId") Integer templateId) {

        Map<String, Object> result = templateService.validateTemplatePlaceholders(file, templateId);
        int code = (int) result.get("code");

        if (code == 200) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(code).body(result);
        }
    }

    @PostMapping("/apply-dates")
    public ResponseEntity<Map<String, Object>> applyDatesToTemplates(
            @RequestBody Map<String, String> dateParams,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        try {
            AuthUtil.getUserIdFromToken(request);

            // 这里可以实现具体的业务逻辑
            // 暂时返回成功
            response.put("code", 200);
            response.put("message", "日期已应用到所有相关模板");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "应用失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
