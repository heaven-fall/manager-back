package com.world.back.serviceImpl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.world.back.entity.user.*;
import com.world.back.mapper.InstituteMapper;
import com.world.back.mapper.LoginMapper;
import com.world.back.entity.res.LoginResponse;
import com.world.back.mapper.UserMapper;
import com.world.back.service.LoginService;
import com.world.back.utils.EntityHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LoginServiceImpl implements LoginService {

  @Autowired
  private LoginMapper loginMapper;
  @Autowired
  private InstituteMapper instituteMapper;
  @Autowired
  private UserMapper userMapper;
  @Override
  public LoginResponse login(String username, String password) {
    // 1. 查询基础用户信息
    BaseUser baseUser = loginMapper.findBaseUser(username, password);
    if (baseUser == null) {
      return null;
    }

    // 2. 根据角色构建不同的用户对象
    switch (baseUser.getRole()) {
      case 0: // 超级管理员
        return buildAdminResponse(baseUser);

      case 1: // 院系管理员
        return buildInstituteAdminResponse(baseUser);

      default:
        return null;
    }
  }

  @Override
  public LoginResponse loginWithYear(String username, String password, Integer year) {

    // 1. 验证教师登录参数
    validateTeacherLogin(username, year);

    // 2. 查询基础用户信息
    BaseUser baseUser = loginMapper.findBaseUser(username, password);

    if (baseUser == null) {
      throw new IllegalArgumentException("用户名或密码错误");
    }

    // 3. 验证是否为教师角色
    if (baseUser.getRole() != 2) {
      throw new IllegalArgumentException("该账号不是教师账号");
    }

    List<Map<String, Object>> years = loginMapper.findDefenseYears(username);
    Map<String, Object> yearinfo = null;
    for (Map<String, Object> map : years) {
      if (Objects.equals((Integer)map.get("year"), year)){
        yearinfo = map;
      }
    }

    if (yearinfo == null) {

      throw new IllegalArgumentException("该教师没有" + year + "年的答辩权限");
    }

    return buildTeacherResponse(baseUser, yearinfo);
  }


  private void validateTeacherLogin(String username, Integer year) {
    if (username == null || username.trim().isEmpty()) {
      throw new IllegalArgumentException("教师编号不能为空");
    }

    if (!username.matches("\\d+")) {
      throw new IllegalArgumentException("教师编号必须为纯数字");
    }

    if (year == null) {
      throw new IllegalArgumentException("答辩年份不能为空");
    }

    // 验证年份格式
    if (year < 2000 || year > 2100) {
      throw new IllegalArgumentException("答辩年份格式不正确");
    }
  }

  private LoginResponse buildAdminResponse(BaseUser baseUser) {
    Admin admin = new Admin();
    copyBaseUserProperties(admin, baseUser);
    return new LoginResponse("admin", admin);
  }

  private LoginResponse buildInstituteAdminResponse(BaseUser baseUser) {
    InstituteAdmin instAdmin = new InstituteAdmin();
    copyBaseUserProperties(instAdmin, baseUser);

    // 修复：使用 baseUser 而不是未定义的 admin
    List<Integer> instituteIds = instituteMapper.findInstituteIdsByUserId(baseUser.getId());

    if (instituteIds != null && !instituteIds.isEmpty()) {
      Integer instituteId = instituteIds.get(0);
      String instituteName = instituteMapper.findInstituteNameById(instituteId);
      instAdmin.setInstId(instituteId);
    }

    return new LoginResponse("instAdmin", instAdmin);
  }

  public boolean checkTeacherExists(String teacherId) {
    try {
      // 查询教师是否存在
      BaseUser user = loginMapper.findUserById(teacherId);
      if (user == null) {
        return false;
      }

      return user.getRole() == 2; // 确保是教师角色
    } catch (Exception e) {
      return false;
    }
  }
  
  private LoginResponse buildTeacherResponse(BaseUser baseUser, Map<String, Object> yearinfo) {
    Teacher teacher = new Teacher();
    copyBaseUserProperties(teacher, baseUser);

    // 查询教师所属院系
    Integer instituteId = loginMapper.findInstituteIdByUserId(baseUser.getId());
    if (instituteId != null) {
      teacher.setInstId(instituteId);
      String instituteName = loginMapper.findInstituteNameById(instituteId);
      teacher.setInstituteName(instituteName);
    }

    // 检查是否为答辩组长
    boolean isDefenseLeader = !Objects.equals(yearinfo.get("is_defense_leader"), false);
    String userType = "teacher";

    teacher.setIsDefenseLeader(isDefenseLeader);
    teacher.setGroupYear((Integer) yearinfo.get("year"));
    teacher.setGroupId((Integer)yearinfo.get("group_id"));
    teacher.setInstituteName(instituteMapper.getInstituteNameById(userMapper.getInstIdByUserId((String)yearinfo.get("teacher_id"))));

    if (isDefenseLeader) {
      // 如果是答辩组长，创建DefenseLeader对象
      DefenseLeader defenseLeader = EntityHelper.buildDefenseLeader(teacher,teacher.getGroupYear());
      userType = "defenseLeader";
      return new LoginResponse(userType, defenseLeader);
    } else {
      userType = "teacher";
      return new LoginResponse(userType, teacher);
    }
  }

  @Override
  public List<Integer> getTeacherDefenseYears(String teacherId) {
    if (teacherId == null || teacherId.trim().isEmpty()) {
      throw new IllegalArgumentException("教师编号不能为空");
    }

    if (!checkTeacherExists(teacherId)) {
      throw new IllegalArgumentException("该教师不存在或不是教师角色");
    }

    List<Integer> years = loginMapper.findDefenseYearsByTeacherId(teacherId);

    for (Map<String, Object> map : loginMapper.findDefenseYears(teacherId)){
      years.add((Integer) map.get("year"));
    }
    

    if (CollectionUtils.isNotEmpty(years)) {
      return years.stream()
              .distinct()
              .sorted((a, b) -> b - a)
              .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  private void copyBaseUserProperties(BaseUser target, BaseUser source) {
    target.setId(source.getId());
    target.setPwd(source.getPwd());
    target.setRole(source.getRole());
    target.setRealName(source.getRealName());
  }
}
