package com.world.back.service;

import com.world.back.entity.Student;
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
    
    List<Student> getStudentByGid(Integer group_id);
    
    
}
