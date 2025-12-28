package com.world.back.service;

import com.world.back.entity.info.DefenseGroupInfo;
import com.world.back.entity.info.DefenseInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface DefenseService
{
    void yearAdd(Integer year);

    void yearDelete(Integer year);

    List<Map<String, Object>> yearAll();

    Integer getCountByYear(Integer year);

    Integer getStudentCountByYear(Integer year);

    // 获取教师所在的当前答辩小组信息
    DefenseGroupInfo getCurrentGroup(String teacherId);

    // 获取答辩小组的学生列表
    List<DefenseInfo> getGroupStudents(Integer groupId, String teacherId);


    // 获取答辩小组的学生列表（供答辩组长使用）
    List<DefenseInfo> getGroupStudentsForLeader(Integer groupId);
}
