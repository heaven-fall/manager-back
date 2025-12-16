package com.world.back.service;

import com.world.back.entity.*;
import org.springframework.stereotype.Service;

@Service
public interface LoginService
{
  Admin Adminlogin(String username, String password);

  InstAdmin InstAdminlogin(String username, String password);

  Teacher Teacherlogin(String username, String password);

  DefenseLeader DefenseLeaderlogin(String username, String password);
}
