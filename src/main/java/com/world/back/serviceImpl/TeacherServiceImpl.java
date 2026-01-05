package com.world.back.serviceImpl;

import com.world.back.entity.Student;
import com.world.back.entity.user.Teacher;
import com.world.back.mapper.InstituteMapper;
import com.world.back.mapper.StudentMapper;
import com.world.back.mapper.TeacherMapper;
import com.world.back.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private InstituteMapper instituteMapper;
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public List<Teacher> getTeacherList(Integer instituteId) {

        List<Teacher> teacherList = teacherMapper.selectTeacherList(instituteId);
        
        return teacherList;
    }

    @Override
    public Teacher getTeacherById(String id) {
        Teacher teacher = teacherMapper.selectTeacherById(id);
        return teacher;
    }

    @Override
    @Transactional
    public boolean createTeacher(Teacher teacher) {
        try {
            System.out.println(">>> TeacherServiceImpl.createTeacher 开始");
            System.out.println("教师信息: " + teacher);

            // 1. 验证必填字段
            if (teacher.getId() == null || teacher.getId().trim().isEmpty()) {
                throw new RuntimeException("教师工号不能为空");
            }

            if (teacher.getRealName() == null || teacher.getRealName().trim().isEmpty()) {
                throw new RuntimeException("教师姓名不能为空");
            }

            if (teacher.getInstId() == null) {
                throw new RuntimeException("院系ID不能为空");
            }

            // 2. 检查教师是否已存在
            int existsCount = teacherMapper.checkTeacherExists(teacher.getId());
            System.out.println("检查工号是否存在: " + teacher.getId() + " -> " + existsCount);

            if (existsCount > 0) {
                throw new RuntimeException("教师工号已存在: " + teacher.getId());
            }

            // 3. 检查院系是否存在
            // 可以添加院系存在性检查（如果需要）
            // if (!instituteMapper.checkInstituteExists(teacher.getInstId())) {
            //     throw new RuntimeException("院系不存在: " + teacher.getInstId());
            // }

            // 4. 设置默认值
            teacher.setPwd("123456"); // 默认密码
            teacher.setRole(2); // 教师角色

            System.out.println("设置后教师信息: " + teacher);

            // 5. 插入用户表
            int result = teacherMapper.insertTeacher(teacher);
            System.out.println("插入教师结果: " + result);

            if (result <= 0) {
                System.out.println("插入教师失败");
                return false;
            }

            // 6. 添加用户-院系关系
            System.out.println("添加院系关系: teacherId=" + teacher.getId() + ", instId=" + teacher.getInstId());
            int relResult = teacherMapper.insertUserInstituteRelation(teacher.getId(), teacher.getInstId());
            System.out.println("添加院系关系结果: " + relResult);

            if (relResult <= 0) {
                throw new RuntimeException("添加院系关系失败");
            }

            System.out.println(">>> TeacherServiceImpl.createTeacher 成功");
            return true;
        } catch (Exception e) {
            System.out.println(">>> TeacherServiceImpl.createTeacher 异常: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("创建教师失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean deleteTeacher(String id) {
        try {
            teacherMapper.deleteAllTeacherGroupRelations(id);
            teacherMapper.deleteUserInstituteRelation(id);
            int result = teacherMapper.deleteTeacher(id);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除教师失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean setDefenseLeader(Integer groupId, String teacherId) {
        try {
            // 检查该小组是否已有组长
            String currentLeader = teacherMapper.getDefenseLeaderByGroupId(groupId);
            if (currentLeader != null && !currentLeader.equals(teacherId)) {
                throw new RuntimeException("该小组已有答辩组长（工号：" + currentLeader + "）");
            }

            if (currentLeader != null && currentLeader.equals(teacherId)) {
                return true; // 已经是组长
            }

            teacherMapper.setDefenseLeader(groupId, teacherId);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("设置答辩组长失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean clearDefenseLeader(Integer groupId) {
        try {
            return teacherMapper.clearDefenseLeader(groupId) > 0;
        } catch (Exception e) {
            throw new RuntimeException("清除组长失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Student> getGuidedStudents(String teacher_id, Integer year)
    {
        List<Student> students = teacherMapper.getGuidedStudents(teacher_id, year);
        for (Student student : students) {
            student.setInstituteName(instituteMapper.getInstituteNameById(student.getInstituteId()));
            Map<String, Object> dbinfo = studentMapper.getDbInfoById(student.getId());
            student.setDefenseTitle((String) dbinfo.get("title"));
            student.setDefenseGroupId((Integer) dbinfo.get("gid"));
            student.setType((Integer) dbinfo.get("type"));
            student.setSummary((String) dbinfo.get("summary"));
        }
        return students;
    }
    
    @Override
    public Boolean addGuideStudent(String teacher_id, String student_id, Integer year)
    {
        if (teacherMapper.checkGuideExist(teacher_id, student_id, year))
        {
            return false;
        }
        teacherMapper.addGuideStudent(teacher_id, student_id, year);
        return true;
    }
    
    @Override
    public Long getTeacherCount(Integer instituteId) {
        return teacherMapper.countTeachers(instituteId, null);
    }

    @Override
    @Transactional
    public boolean addTeacherToGroup(String teacherId, Integer groupId, Boolean isDefenseLeader) throws RuntimeException{
        if (teacherMapper.checkGroupExists(groupId) == 0) {
            throw new RuntimeException("小组不存在");
        }

        Integer groupYear = teacherMapper.getGroupYearById(groupId);
        if (groupYear == null) {
            throw new RuntimeException("无法获取小组年份");
        }

        int count = teacherMapper.checkTeacherInYear(teacherId, groupYear);
        if (count > 0) {
            throw new RuntimeException("该教师已加入" + groupYear + "年的答辩小组，同一年只能参加一个小组");
        }

        // 如果设置为组长，检查该小组是否已有组长
        if (isDefenseLeader != null && isDefenseLeader) {
            // 检查该小组是否已有组长
            String currentLeader = teacherMapper.getDefenseLeaderByGroupId(groupId);
            if (currentLeader != null) {
                return false;
            }
        }

        int result = teacherMapper.insertTeacherGroupRelation(teacherId, groupId, isDefenseLeader);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean removeTeacherFromGroup(String teacherId, Integer groupId) {
        try {
            String currentLeader = teacherMapper.getDefenseLeaderByGroupId(groupId);
            if (currentLeader != null && currentLeader.equals(teacherId)) {
                // 如果是组长，先清除组长标记
                clearDefenseLeader(groupId);
            }

            int result = teacherMapper.deleteTeacherGroupRelation(teacherId, groupId);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("从小组移除教师失败: " + e.getMessage());
        }
    }

    @Override
    public List<Integer> getAvailableYears() {
        return teacherMapper.selectAvailableYears();
    }

    @Override
    public List<Integer> getGroupsByYear(Integer year) {
        return teacherMapper.selectGroupsByYear(year);
    }

    @Override
    public boolean isTeacherInYear(String teacherId, Integer year) {
        return teacherMapper.checkTeacherInYear(teacherId, year) > 0;
    }
    
    
}
