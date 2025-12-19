package com.world.back.serviceImpl;

import com.world.back.mapper.UserMapper;
import com.world.back.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService
{
  @Autowired
  private UserMapper userMapper;
  @Override
  public Long getAdminCount()
  {
    return userMapper.getAdminCount();
  }
  
  @Override
  public Long getTeacherCount()
  {
    return userMapper.getTeacherCount();
  }
}
