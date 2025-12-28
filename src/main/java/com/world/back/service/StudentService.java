package com.world.back.service;

import com.world.back.entity.Student;

import java.util.List;
import java.util.Map;

public interface StudentService {
    // 获取学生列表
    List<Map<String, Object>> getStudentList(Long instituteId);
    
    List<Map<String, Object>> getStudentListPage(Integer instituteId, Integer currentpage, Integer pagesize);

    // 根据ID获取学生
    Student getStudentById(String id);

    List<Student> getStudentByInstituteId(Integer institute_id);

    // 创建学生
    boolean createStudent(Student student);

    // 更新学生
    boolean updateStudent(Student student);

    // 删除学生
    boolean deleteStudent(String id);

    // 分配答辩小组
    boolean assignGroup(String studentId, Integer groupId, Integer type);

    // 验证学号是否重复
    boolean isStudentIdDuplicate(String studentId, String excludeId);
    
    String getTeacherById(String student_id);
    
    
    Integer getGidBySid(String sid);
    
    Integer getCount(Integer instituteId);
    
    Integer getUnassignCount(Integer instituteId);
    
    Map<String, Object> getDbInfoById(String id);
}
