package com.world.back.serviceImpl;

import com.world.back.entity.user.Teacher;
import com.world.back.mapper.TeacherMapper;
import com.world.back.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public List<Map<String, Object>> getTeacherList(Integer instituteId) {
        return teacherMapper.getTeacherListByInstituteId(instituteId);
    }
    
    @Override
    public List<Map<String, Object>> getTeacherList()
    {
        return teacherMapper.getTeacherList();
    }

    @Override
    public Teacher getTeacherById(String id) {
        return teacherMapper.selectTeacherById(id);
    }

    @Override
    @Transactional
    public boolean createTeacher(Teacher teacher) {
        try {
            // 设置默认密码
            teacher.setPwd("123456");

            // 默认设为普通教师角色（2），不设管理员权限
            teacher.setRole(2);

            // 注意：确保 realName 不为空
            if (teacher.getRealName() == null || teacher.getRealName().trim().isEmpty()) {
                throw new RuntimeException("教师姓名不能为空");
            }

            // 插入教师信息
            int result = teacherMapper.insertTeacher(teacher);
            if (result <= 0) {
                return false;
            }

            // 插入院系关系
            if (teacher.getInstituteId() != null) {
                teacherMapper.insertUserInstituteRelation(teacher.getId(), teacher.getInstituteId());
            }

            return true;
        } catch (Exception e) {
            throw new RuntimeException("创建教师失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean updateTeacher(Teacher teacher) {
        try {
            // 更新基本信息
            int result = teacherMapper.updateTeacher(teacher);
            if (result <= 0) {
                return false;
            }

            return true;
        } catch (Exception e) {
            throw new RuntimeException("更新教师失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean deleteTeacher(String id) {
        try {
            // 先删除院系关系
            teacherMapper.deleteUserInstituteRelation(id);

            // 再删除教师
            int result = teacherMapper.deleteTeacher(id);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除教师失败: " + e.getMessage());
        }
    }

    @Override
    public boolean setDefenseLeader(Integer groupId, String teacherId) {
        try {
            int result = teacherMapper.setDefenseLeader(groupId, teacherId);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("设置答辩组长失败: " + e.getMessage());
        }
    }

    @Override
    public Long getTeacherCount(Integer instituteId) {
        return teacherMapper.countTeachers(instituteId, null);
    }
}
