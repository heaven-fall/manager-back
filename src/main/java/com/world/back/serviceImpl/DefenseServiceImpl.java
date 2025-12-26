package com.world.back.serviceImpl;

import com.world.back.entity.Student;
import com.world.back.mapper.DefenseMapper;
import com.world.back.service.DefenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DefenseServiceImpl implements DefenseService
{
    @Autowired
    private DefenseMapper defenseMapper;
    @Override
    public void yearAdd(Integer year)
    {
        defenseMapper.yearAdd(year);
    }
    
    @Override
    public void yearDelete(Integer year)
    {
        defenseMapper.yearDelete(year);
    }
    
    @Override
    public List<Map<String, Object>> yearAll()
    {
        return defenseMapper.yearAll();
    }
    
    @Override
    public Integer getCountByYear(Integer year)
    {
        return defenseMapper.getCountByYear(year);
    }
    
    @Override
    public Integer getStudentCountByYear(Integer year)
    {
        return defenseMapper.getStudentCountByYear(year);
    }
    
    @Override
    public List<Student> getStudentByGid(Integer group_id)
    {
        return defenseMapper.getStudentByGid(group_id);
    }
    
    
}
