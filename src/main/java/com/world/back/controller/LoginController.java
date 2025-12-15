package com.world.back.controller;

import com.world.back.entity.Login;
import com.world.back.entity.User;
import com.world.back.entity.Result;
import com.world.back.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import util.ResultHandler;
import java.util.Objects;


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
    
    User user = loginService.login(username, password);
    if (Objects.isNull(user))
    {
      return ResultHandler.buildResult(400, "请求失败", null);
    }
    return ResultHandler.buildResult(200, "登录成功", user);
  }
}
