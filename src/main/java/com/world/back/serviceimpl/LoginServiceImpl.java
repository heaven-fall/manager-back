package com.world.back.serviceimpl;

import com.world.back.entity.User;
import com.world.back.mapper.LoginMapper;
import com.world.back.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginServiceImpl implements LoginService
{
  @Autowired
  private LoginMapper loginMapper;
  
  @Override
  public User login(String username, String password)
  {
    return loginMapper.login(username, password);
  }
}
