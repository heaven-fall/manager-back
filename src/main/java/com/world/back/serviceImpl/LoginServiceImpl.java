package com.world.back.serviceImpl;

import com.world.back.entity.Admin;
import com.world.back.entity.DefenseLeader;
import com.world.back.entity.InstAdmin;
import com.world.back.entity.Teacher;
import com.world.back.mapper.LoginMapper;
import com.world.back.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService
{
  @Autowired
  private LoginMapper loginMapper;

  @Override
  public Admin Adminlogin(String username, String password) {
    return loginMapper.adminLogin(username, password);
  }

  @Override
  public InstAdmin InstAdminlogin(String username, String password) {
    return loginMapper.instAdminLogin(username, password);
  }

  @Override
  public Teacher Teacherlogin(String username, String password) {
    return loginMapper.teacherLogin(Integer.valueOf(username), password);
  }

  @Override
  public DefenseLeader DefenseLeaderlogin(String username, String password) {
    return loginMapper.defenseLeaderLogin(Integer.valueOf(username), password);
  }


}
