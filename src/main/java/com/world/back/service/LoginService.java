package com.world.back.service;

import com.world.back.entity.res.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public interface LoginService
{
  LoginResponse login(String username, String password);
  // 带年份的登录方法
  LoginResponse loginWithYear(String username, String password, Integer year);
}