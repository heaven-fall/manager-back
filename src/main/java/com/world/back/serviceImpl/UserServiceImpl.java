package com.world.back.serviceImpl;

import com.world.back.entity.user.Admin;
import com.world.back.entity.user.BaseUser;
import com.world.back.entity.user.InstituteAdmin;
import com.world.back.mapper.UserMapper;
import com.world.back.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService
{
  @Autowired
  private UserMapper userMapper;
  @Override
  public Long getAdminCount()
  {
    return userMapper.getAdminCount();
  }
  
  @Override
  public Long getTeacherCount()
  {
    return userMapper.getTeacherCount();
  }
  
  @Override
  public List<BaseUser> getAllTeachers()
  {
    return userMapper.getAllTeachers();
  }
  
  @Override
  public Boolean createAdmin(InstituteAdmin admin)
  {
    userMapper.createAdmin(admin.getId(), admin.getRealName(), admin.getRole(), admin.getPwd());
    userMapper.createUserInstRel(admin.getId(), admin.getInstId());
    return true;
  }
  
  @Override
  public Boolean updateAdmin(String realName, String username, String phone, String email)
  {
    userMapper.updateAdmin(realName, username, phone, email);
    return true;
  }
  
  @Override
  public List<Admin> getAllAdmins()
  {
    return userMapper.getAllAdmins();
  }

  @Override
  public boolean changePassword(String userId, String oldPassword, String newPassword) {
    // 1. 验证用户存在
    BaseUser user = userMapper.getUserById(userId);
    if (user == null) {
      return false;
    }

    // 2. 验证原密码是否正确
    if (!user.getPwd().equals(oldPassword)) {
      return false;
    }

    // 3. 更新密码
    int rows = userMapper.updatePassword(userId, newPassword);
    return rows > 0;
  }
}
