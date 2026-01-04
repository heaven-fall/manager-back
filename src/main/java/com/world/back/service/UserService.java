package com.world.back.service;

import com.world.back.entity.res.Result;
import com.world.back.entity.user.Admin;
import com.world.back.entity.user.BaseUser;
import com.world.back.entity.user.InstituteAdmin;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface UserService
{
  Integer getAdminCount();
  Boolean createAdmin(InstituteAdmin admin);
  Boolean updateAdmin(String realName, String username, String phone, String email);
  List<Admin> getAllAdmins();

  boolean changePassword(String userId, String oldPassword, String newPassword);

    String getNameById(String id);

    Result<String> getCurrentSignature(String userId);

  Result<String> uploadSignature(MultipartFile file, String userId);

  Boolean resetPassword(String id);
  Boolean deleteInstituteAdmin(String id);
  Boolean createUserInstRel(String userId, Integer instituteId);
  Boolean deleteUserInstRel(String userId, Integer instituteId);
}
