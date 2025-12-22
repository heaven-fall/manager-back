package com.world.back.service;

import com.world.back.entity.user.Admin;
import com.world.back.entity.user.BaseUser;
import com.world.back.entity.user.InstituteAdmin;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService
{
  Long getAdminCount();
  Long getTeacherCount();
  List<BaseUser> getAllTeachers();
  Boolean createAdmin(InstituteAdmin admin);
  Boolean updateAdmin(String realName, String username, String phone, String email);
  List<Admin> getAllAdmins();
}
