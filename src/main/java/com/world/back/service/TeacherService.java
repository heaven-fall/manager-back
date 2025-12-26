package com.world.back.service;

import com.world.back.entity.user.Teacher;
import java.util.List;
import java.util.Map;

public interface TeacherService {
    Map<String, Object> getTeacherList(Integer instituteId, Integer page, Integer size, String search);
    Teacher getTeacherById(String id);
    boolean createTeacher(Teacher teacher);
    boolean deleteTeacher(String id);
    boolean setDefenseLeader(Integer groupId, String teacherId);
    Long getTeacherCount(Integer instituteId);
    boolean addTeacherToGroup(String teacherId, Integer groupId, Boolean isDefenseLeader);
    boolean removeTeacherFromGroup(String teacherId, Integer groupId);
    List<Integer> getAvailableYears();
    List<Integer> getGroupsByYear(Integer year);
    boolean isTeacherInYear(String teacherId, Integer year);
    boolean clearDefenseLeader(Integer groupId);
}