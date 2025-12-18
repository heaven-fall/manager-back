package com.world.back.service;

import com.world.back.entity.*;
import org.springframework.stereotype.Service;

@Service
public interface LoginService
{
  // 新增统一登录方法
  LoginResponse login(String username, String password);
}