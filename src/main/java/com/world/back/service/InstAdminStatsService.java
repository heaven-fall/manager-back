package com.world.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class InstAdminStatsService {

    private final StudentService studentService;
    private final TeacherService teacherService;

    /**
     * 获取院系管理员首页统计数据
     */
    public Map<String, Object> getDashboardStats(Long instituteId) {
        Map<String, Object> stats = new HashMap<>();

        try {
            // 1. 获取学生数量
            Map<String, Object> studentList = studentService.getStudentList(instituteId);
            if (studentList != null && studentList.containsKey("total")) {
                stats.put("student_count", studentList.get("total"));
            }

            // 2. 获取教师数量
            Long teacherCount = teacherService.getTeacherCount(instituteId.intValue());
            stats.put("teacher_count", teacherCount);

            // 3. 获取答辩年份数量


            // 4. 获取答辩小组数量


        } catch (Exception e) {
            // 设置默认值
            stats.put("student_count", 0);
            stats.put("teacher_count", 0L);
            stats.put("defense_year_count", 0);
            stats.put("group_count", 0);
            stats.put("todos", new ArrayList<>());
        }

        return stats;
    }


}
