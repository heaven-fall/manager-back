package com.world.back.service;

import com.world.back.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface LoginService
{
  User login(String username, String password);
}
