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
            if (teacherMapper.checkTeacherExists(teacher.getId()) > 0) {
                throw new RuntimeException("教师工号已存在");
            }

            teacher.setPwd("123456");
            teacher.setRole(2);

            if (teacher.getRealName() == null || teacher.getRealName().trim().isEmpty()) {
                throw new RuntimeException("教师姓名不能为空");
            }

            int result = teacherMapper.insertTeacher(teacher);
            if (result <= 0) {
                return false;
            }

            if (teacher.getInstId() != null) {
                teacherMapper.insertUserInstituteRelation(teacher.getId(), teacher.getInstId());
            }

            return true;
        } catch (Exception e) {
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
    public boolean addTeacherToGroup(String teacherId, Integer groupId, Boolean isDefenseLeader) {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException("添加教师到小组失败: " + e.getMessage());
        }
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
