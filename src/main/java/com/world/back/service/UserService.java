package com.world.back.service;

import com.world.back.entity.user.BaseUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService
{
  Long getAdminCount();
  Long getTeacherCount();
  List<BaseUser> getAllTeachers();
}
