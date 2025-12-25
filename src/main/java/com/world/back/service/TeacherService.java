package com.world.back.service;

import com.world.back.entity.user.Teacher;
import java.util.List;
import java.util.Map;

public interface TeacherService {

    List<Map<String, Object>> getTeacherList(Integer instituteId);
    
    List<Map<String, Object>> getTeacherList();
    
    // 获取教师详情
    Teacher getTeacherById(String id);

    // 创建教师
    boolean createTeacher(Teacher teacher);

    // 更新教师
    boolean updateTeacher(Teacher teacher);

    // 删除教师
    boolean deleteTeacher(String id);

    // 设置答辩组长
    boolean setDefenseLeader(Integer groupId, String teacherId);

    // 获取教师总数
    Long getTeacherCount(Integer instituteId);
}
