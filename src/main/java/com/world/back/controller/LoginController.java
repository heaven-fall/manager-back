package com.world.back.controller;

import com.world.back.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.Objects;

class Result {
  //响应码
  private int code;
  
  public Result(int code) {
    this.code = code;
  }
  
  public int getCode() {
    return code;
  }
  
  public void setCode(int code) {
    this.code = code;
  }
  
}

@Controller
public class LoginController
{
  @CrossOrigin
  @PostMapping("api/login")
  @ResponseBody
  public Result login(@RequestBody User requestUser)
  {
    String username = requestUser.getUsername();
    username = HtmlUtils.htmlEscape(username);
    
    if (!Objects.equals("admin", username) || !Objects.equals("123456", requestUser.getPassword())) {
      String message = "账号密码错误";
      System.out.println("test");
      return new Result(400);
    } else {
      return new Result(200);
    }
  }
}
