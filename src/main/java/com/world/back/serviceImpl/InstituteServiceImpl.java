package com.world.back.serviceImpl;

import com.world.back.entity.Institute;
import com.world.back.entity.res.Result;
import com.world.back.mapper.InstituteMapper;
import com.world.back.service.InstituteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstituteServiceImpl implements InstituteService
{
  @Autowired
  private InstituteMapper instituteMapper;
  @Override
  public Long getInstituteCount()
  {
    return instituteMapper.getInstituteCount();
  }
  
  @Override
  public List<Institute> getAll()
  {
    List<Institute> list = instituteMapper.getAll();
    for (Institute institute : list)
    {
      institute.setTeacherCount(instituteMapper.getTeacherCount(institute.getId()));
      institute.setStudentCount(instituteMapper.getStudentCount(institute.getId()));
    }
    return list;
  }
  
  @Override
  public void addInstitute(Institute institute)
  {
    instituteMapper.addInstitute(institute.getName(), institute.getAdminId());
  }
  
}
