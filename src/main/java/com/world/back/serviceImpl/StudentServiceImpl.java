package com.world.back.serviceImpl;

import com.world.back.entity.Student;
import com.world.back.mapper.GroupMapper;
import com.world.back.mapper.StudentMapper;
import com.world.back.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    @Autowired
    private final StudentMapper studentMapper;
    @Autowired
    private final GroupMapper groupMapper;

    @Override
    public Map<String, Object> getStudentList(Long instituteId) {
        try {
            Map<String, Object> result = new HashMap<>();

            List<Map<String, Object>> list = studentMapper.findListByInstitute(instituteId);

            // 查询总数
            Integer total = list.size();

            // 处理字段映射
            list.forEach(item -> {
                // 将下划线字段转为驼峰
                if (item.containsKey("real_name")) {
                    item.put("name", item.get("real_name"));
                    item.put("realName", item.get("real_name"));
                    item.remove("real_name");
                }

                // 处理电话字段
                if (item.containsKey("tel")) {
                    item.put("phone", item.get("tel"));
                }

                // 添加前端需要的字段
                item.put("studentId", item.get("id"));  // 学号
                item.put("dbgroup", studentMapper.findGroupIdByStudentId(item.get("id").toString()));
            });

            result.put("list", list);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("获取学生列表失败", e);
            throw new RuntimeException("获取学生列表失败: " + e.getMessage());
        }
    }

    @Override
    public Student getStudentById(String id) {
        try {
            return studentMapper.findById(id);
        } catch (Exception e) {
            log.error("获取学生信息失败", e);
            throw new RuntimeException("获取学生信息失败");
        }
    }

    @Override
    public List<Student> getStudentByInstituteId(Integer institute_id)
    {
        return studentMapper.getStudentByInstituteId(institute_id);
    }

    @Override
    @Transactional
    public boolean createStudent(Student student) {
        try {
            // 检查学号是否已存在
            int exists = studentMapper.checkStudentIdExists(student.getId());
            if (exists > 0) {
                throw new RuntimeException("学号已存在");
            }

            int result = studentMapper.insert(student);
            return result > 0;
        } catch (Exception e) {
            log.error("创建学生失败", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean updateStudent(Student student) {
        try {
            int result = studentMapper.update(student);
            return result > 0;
        } catch (Exception e) {
            log.error("更新学生失败", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean deleteStudent(String id) {
        try {
            // 先删除关联的答辩信息
            studentMapper.removeGroupAssignment(id);

            int result = studentMapper.deleteById(id);
            return result > 0;
        } catch (Exception e) {
            log.error("删除学生失败", e);
            throw new RuntimeException("删除学生失败");
        }
    }

    @Override
    @Transactional
    public boolean assignGroup(String studentId, Integer groupId) {
        try {
            if (groupMapper.getStudentByGid(groupId).size() == groupMapper.getMaxStudentCountByGid(groupId))
            {
                return false;
            }
            // 先移除旧的分配
            studentMapper.removeGroupAssignment(studentId);

            // 添加新的分配
            int result = studentMapper.assignGroup(studentId, groupId);
            return result > 0;
        } catch (Exception e) {
            log.error("分配答辩小组失败", e);
            throw new RuntimeException("分配答辩小组失败");
        }
    }

    @Override
    public boolean isStudentIdDuplicate(String studentId, String excludeId) {
        try {
            if (excludeId != null && excludeId.equals(studentId)) {
                return false;
            }
            int exists = studentMapper.checkStudentIdExists(studentId);
            return exists > 0;
        } catch (Exception e) {
            log.error("检查学号重复失败", e);
            return true;
        }
    }
}
