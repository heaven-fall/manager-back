package com.world.back.service;

import com.world.back.entity.res.LoginResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LoginService
{
  LoginResponse login(String username, String password);
  // 带年份的登录方法
  LoginResponse loginWithYear(String username, String password, Integer year);

  List<Integer> getTeacherDefenseYears(String teacherId);
}