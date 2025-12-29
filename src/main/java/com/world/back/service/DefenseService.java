package com.world.back.service;

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

    List<Map<String, Object>> getGroupFirstStudents(Integer year);

    Boolean saveMajorScore(Integer groupId, String studentId, Double majorScore);

    Map<String, Object> SaveCoefficients(Integer year);

    Double getAdjustmentCoefficient(Integer groupId);
}
