package com.world.back.controller;

import com.world.back.entity.res.Result;
import com.world.back.serviceImpl.InstAdminStatsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/inst-admin")
@RequiredArgsConstructor
public class InstAdminStatsController {

    private final InstAdminStatsServiceImpl statsService;
    @GetMapping("/stats")
    public Result<Map<String, Object>> getDashboardStats(
            @RequestParam("institute_id") Long instituteId) {
        try {
            Map<String, Object> stats = statsService.getDashboardStats(instituteId);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats/simple")
    public Result<Map<String, Object>> getSimpleStats(
            @RequestParam("institute_id") Long instituteId) {
        try {
            Map<String, Object> stats = statsService.getDashboardStats(instituteId);

            // 只保留数量信息
            Map<String, Object> simpleStats = new HashMap<>();
            simpleStats.put("studentCount", stats.get("student_count"));
            simpleStats.put("teacherCount", stats.get("teacher_count"));
            simpleStats.put("defenseYearCount", stats.get("defense_year_count"));
            simpleStats.put("groupCount", stats.get("group_count"));

            return Result.success(simpleStats);
        } catch (Exception e) {
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }
}
