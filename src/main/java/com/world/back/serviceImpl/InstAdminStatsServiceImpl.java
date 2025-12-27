package com.world.back.serviceImpl;

import com.world.back.service.DefenseService;
import com.world.back.service.GroupService;
import com.world.back.service.StudentService;
import com.world.back.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class InstAdminStatsServiceImpl
{

    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private DefenseService defenseService;
    @Autowired
    private GroupService groupService;
    /**
     * 获取院系管理员首页统计数据
     */
    public Map<String, Object> getDashboardStats(Long instituteId) {
        Map<String, Object> stats = new HashMap<>();

        try {
            // 1. 获取学生数量
            stats.put("student_count", studentService.getStudentList(instituteId).size());

            // 2. 获取教师数量
            Long teacherCount = teacherService.getTeacherCount(instituteId.intValue());
            stats.put("teacher_count", teacherCount);

            // 3. 获取答辩年份数量
            stats.put("defense_year_count", defenseService.yearAll().size());

            // 4. 获取答辩小组数量
            stats.put("group_count", groupService.getAllGroups(0).size());

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
