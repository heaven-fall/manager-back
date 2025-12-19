package com.world.back.serviceImpl;

import com.world.back.entity.res.Result;
import com.world.back.mapper.InstituteMapper;
import com.world.back.service.InstituteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
  
}
