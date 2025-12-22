package com.world.back.controller;

import com.world.back.entity.res.*;
import com.world.back.serviceImpl.LoginServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/")
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

    // 使用统一登录方法
    LoginResponse loginResponse = loginService.login(username, password);
    if (loginResponse != null) {
      System.out.println(Result.success(loginResponse).toString());
      return Result.success("登录成功", loginResponse);
    }

    return Result.error("用户名或密码错误");
  }

  // 带年份的登录（用于选择答辩年份后）
  @PostMapping("/loginWithYear")
  public Result<LoginResponse> loginWithYear(@RequestBody LoginWithYearRequest request) {
    LoginResponse loginResponse = loginService.loginWithYear(
            request.getUsername(),
            request.getPassword(),
            request.getDefenseYear()
    );

    if (loginResponse != null) {
      return Result.success("登录成功", loginResponse);
    }

    return Result.error("用户名或密码错误或该年份下无权限");
  }

  @GetMapping("/defenseYears/{teacherId}")
  public Result<List<Integer>> getDefenseYears(@PathVariable String teacherId) {  // 添加 @PathVariable 注解
    try {
      List<Integer> years = loginService.getTeacherDefenseYears(teacherId);
      System.out.println("返回年份数据: " + years);
      return Result.success(years);
    } catch (Exception e) {
      System.out.println("获取年份异常: " + e.getMessage());
      return Result.error("获取答辩年份失败: " + e.getMessage());
    }
  }

  @PostMapping("/changePassword")
  public Result<String> changePassword(@RequestBody ChangePasswordRequest request) {
    // 这里实现修改密码逻辑
    return Result.success("密码修改成功");
  }

  @PostMapping("/logout")
  public Result<String> logout(@RequestBody LogoutRequest request) {
    // 处理登出逻辑
    return Result.success("登出成功");
  }
}