package com.world.back.controller;

import com.world.back.service.DateConfigService;
import com.world.back.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/date-config")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class DateConfigController {

    private final DateConfigService dateConfigService;
    private final AuthUtil authUtil;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> getAllDateConfigs(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 验证token（暂时注释掉，方便测试）
            // String userId = authUtil.getUserIdFromToken(request);

            List<Map<String, Object>> configs = dateConfigService.getAllDateConfigs();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", configs);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取日期配置失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveDateConfigs(
            @RequestBody Map<String, String> dateParams,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        try {
            // 验证token
            // String userId = authUtil.getUserIdFromToken(request);

            if (dateParams == null || dateParams.isEmpty()) {
                response.put("code", 400);
                response.put("message", "参数不能为空");
                return ResponseEntity.status(400).body(response);
            }

            boolean success = dateConfigService.saveDateConfigs(dateParams);
            if (success) {
                response.put("code", 200);
                response.put("message", "保存成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 500);
                response.put("message", "保存失败");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "保存失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetDatesToCurrent(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 验证token
            // String userId = authUtil.getUserIdFromToken(request);

            dateConfigService.resetDatesToCurrent();
            response.put("code", 200);
            response.put("message", "重置成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "重置失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/defense-date")
    public ResponseEntity<Map<String, Object>> getDefenseDate(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 验证token
            // String userId = authUtil.getUserIdFromToken(request);

            String defenseDate = dateConfigService.getDefenseDate();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", defenseDate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/evaluation-date")
    public ResponseEntity<Map<String, Object>> getEvaluationDate(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 验证token
            // String userId = authUtil.getUserIdFromToken(request);

            String evaluationDate = dateConfigService.getEvaluationDate();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", evaluationDate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}