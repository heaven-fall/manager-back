package com.world.back.serviceImpl;

import com.world.back.entity.*;
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
  public LoginResponse login(String username, String password) {
    // 1. 先尝试超级管理员登录
    Admin admin = loginMapper.adminLogin(username, password);
    if (admin != null) {
      return new LoginResponse("admin", admin);
    }

    // 2. 尝试院系管理员登录
    InstAdmin instAdmin = loginMapper.instAdminLogin(username, password);
    if (instAdmin != null) {
      return new LoginResponse("instAdmin", instAdmin);
    }

    // 3. 尝试教师登录（教师使用ID登录）
    try {
      Integer teacherId = Integer.valueOf(username);
      Teacher teacher = loginMapper.teacherLogin(teacherId, password);
      if (teacher != null) {
        // 检查是否为答辩组长（当前年份）
        int isDefenseLeader = loginMapper.defenseLeaderLogin(teacherId,password).getTeacherId();
        if (isDefenseLeader > 0) {
          return new LoginResponse("defenseLeader", teacher);
        } else {
          return new LoginResponse("teacher", teacher);
        }
      }
    } catch (NumberFormatException e) {
      // 如果不是数字，说明不是教师ID
    }

    return null;
  }

  public Admin Adminlogin(String username, String password) {
    return loginMapper.adminLogin(username, password);
  }


  public InstAdmin InstAdminlogin(String username, String password) {
    return loginMapper.instAdminLogin(username, password);
  }

  public Teacher Teacherlogin(String username, String password) {
    try {
      return loginMapper.teacherLogin(Integer.valueOf(username), password);
    } catch (NumberFormatException e) {
      return null;
    }
  }


  public DefenseLeader DefenseLeaderlogin(String username, String password) {
    return loginMapper.defenseLeaderLogin(Integer.valueOf(username), password);
  }


}
