package com.world.back.controller;

import com.world.back.entity.res.Result;
import com.world.back.entity.user.BaseUser;
import com.world.back.service.UserService;
import com.world.back.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/teacher")
public class TeacherController
{
  @Autowired
  private UserServiceImpl userService;
  
  @GetMapping("/count")
  public Result<Long> getCount()
  {
    return Result.success(userService.getTeacherCount());
  }
  
  @GetMapping("/list")
  public Result<List<BaseUser>> getAllTeachers()
  {
    return Result.success(userService.getAllTeachers());
  }
}
