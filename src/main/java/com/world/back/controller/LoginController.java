package com.world.back.controller;

import com.world.back.entity.*;
import com.world.back.service.LoginService;
import com.world.back.serviceImpl.LoginServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class LoginController
{
  @Autowired
  private LoginServiceImpl loginService;
  @CrossOrigin
  @PostMapping("/login")
  @ResponseBody
  public Result<LoginResponse> login(@RequestBody Login login)
  {
    String username = login.getUsername();
    String password = login.getPassword();

    // 使用新的登录方法
    LoginResponse loginResponse = loginService.login(username, password);

    if (loginResponse != null) {
      return Result.success("登录成功", loginResponse);
    }

    return Result.error("用户名或密码错误");
  }

  // 可以添加一个初始化超级管理员的方法
  @PostMapping("/initAdmin")
  public Result<String> initSuperAdmin() {
    // 这里可以初始化超级管理员账号
    // admin/123456
    return Result.success("超级管理员已初始化");
  }
}
