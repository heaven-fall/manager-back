package com.world.back.controller;

import com.world.back.entity.*;
import com.world.back.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController
{
  @Autowired
  private LoginService loginService;
  @CrossOrigin
  @PostMapping("/login")
  @ResponseBody
  public Result login(@RequestBody Login login)
  {
    String username = login.getUsername();
    String password = login.getPassword();

    Admin AdminRes=loginService.Adminlogin(username, password);
    if(AdminRes!=null){
      return Result.success(AdminRes);
    }

    InstAdmin InstAdminRes=loginService.InstAdminlogin(username, password);
    if(InstAdminRes!=null){
      return Result.success(InstAdminRes);
    }

    Teacher TeacherRes=loginService.Teacherlogin(username, password);
    if(TeacherRes!=null){
      return Result.success(TeacherRes);
    }

    DefenseLeader DefenseLeaderRes=loginService.DefenseLeaderlogin(username, password);
    if(DefenseLeaderRes!=null){
      return Result.success(DefenseLeaderRes);
    }
    return Result.error("用户名或密码错误");
  }
}
