package com.world.back.service;

import com.world.back.entity.Student;

import java.util.Map;

public interface StudentService {
    // 获取学生列表
    Map<String, Object> getStudentList(Long instituteId, String search, Integer page, Integer size);

    // 根据ID获取学生
    Student getStudentById(String id);

    // 根据学号获取学生
    Student getStudentByStudentId(String studentId);

    // 创建学生
    boolean createStudent(Student student);

    // 更新学生
    boolean updateStudent(Student student);

    // 删除学生
    boolean deleteStudent(String id);

    // 分配答辩小组
    boolean assignGroup(String studentId, Long groupId);

    // 验证学号是否重复
    boolean isStudentIdDuplicate(String studentId, String excludeId);
}
