package com.world.back.serviceImpl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.world.back.entity.user.*;
import com.world.back.mapper.LoginMapper;
import com.world.back.entity.res.LoginResponse;
import com.world.back.service.LoginService;
import com.world.back.utils.EntityHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoginServiceImpl implements LoginService {

  @Autowired
  private LoginMapper loginMapper;

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
    System.out.println("教师登录请求 - 用户名: " + username + ", 年份: " + year);

    // 1. 验证教师登录参数
    validateTeacherLogin(username, year);

    // 2. 查询基础用户信息
    BaseUser baseUser = loginMapper.findBaseUser(username, password);
    System.out.println("查询到的用户: " + (baseUser != null ? baseUser.getId() : "null"));

    if (baseUser == null) {
      throw new IllegalArgumentException("用户名或密码错误");
    }

    // 3. 验证是否为教师角色
    if (baseUser.getRole() != 2) {
      throw new IllegalArgumentException("该账号不是教师账号");
    }

    // 4. 验证教师是否有该年份的权限
    boolean hasPermission = checkTeacherYearPermission(baseUser.getId(), year);
    System.out.println("教师 " + baseUser.getId() + " 是否有 " + year + " 年权限: " + hasPermission);

    if (!hasPermission) {
      // 进一步检查原因
      boolean hasTeaStuRel = loginMapper.checkTeacherDefenseYearPermission(baseUser.getId(), year);
      boolean isLeader = loginMapper.checkIsDefenseLeaderByYear(baseUser.getId(), year);
      System.out.println("详细检查 - tea_stu_rel: " + hasTeaStuRel + ", dbgroup: " + isLeader);

      throw new IllegalArgumentException("该教师没有" + year + "年的答辩权限");
    }

    // 5. 构建教师响应（带年份）
    return buildTeacherResponse(baseUser, year);
  }

  public boolean checkTeacherYearPermission(String teacherId, Integer year) {
    // 1. 从 tea_stu_rel 表查询教师是否有该年份的指导记录
    boolean hasPermission = loginMapper.checkTeacherDefenseYearPermission(teacherId, year);

    // 2. 如果没有 tea_stu_rel 表记录，检查是否在该年份是答辩组长
    if (!hasPermission) {
      Boolean isDefenseLeader = loginMapper.checkIsDefenseLeaderByYear(teacherId, year);
      hasPermission = Boolean.TRUE.equals(isDefenseLeader);
    }

    System.out.println("检查权限 - 教师ID: " + teacherId + ", 年份: " + year + ", 是否有权限: " + hasPermission);
    return hasPermission;
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

    // 查询院系信息
    Integer instituteId = loginMapper.findInstituteIdByUserId(baseUser.getId());
    if (instituteId != null) {
      instAdmin.setInstId(instituteId);
    }

    return new LoginResponse("instAdmin", instAdmin);
  }

  public boolean checkTeacherExists(String teacherId) {
    try {
      // 查询教师是否存在
      BaseUser user = loginMapper.findUserById(teacherId);
      if (user == null) {
        System.out.println("教师 " + teacherId + " 不存在");
        return false;
      }

      System.out.println("找到用户: " + user.getId() + ", 角色: " + user.getRole());
      return user.getRole() == 2; // 确保是教师角色
    } catch (Exception e) {
      System.out.println("查询教师时出错: " + e.getMessage());
      return false;
    }
  }
  private LoginResponse buildTeacherResponse(BaseUser baseUser, Integer year) {
    Teacher teacher = new Teacher();
    copyBaseUserProperties(teacher, baseUser);

    // 查询教师所属院系
    Integer instituteId = loginMapper.findInstituteIdByUserId(baseUser.getId());
    if (instituteId != null) {
      teacher.setInstituteId(instituteId);
      String instituteName = loginMapper.findInstituteNameById(instituteId);
      teacher.setInstituteName(instituteName);
    }

    // 检查是否为答辩组长
    Integer groupId = null;
    boolean isDefenseLeader = false;
    String userType = "teacher";

    if (year != null) {
      // 有年份参数，检查该年份是否为答辩组长
      groupId = loginMapper.findGroupIdByLeaderAndYear(baseUser.getId(), year);
      isDefenseLeader = groupId != null;
    } else {
      // 没有年份参数，检查是否有任何年份是答辩组长
      isDefenseLeader = loginMapper.checkIsDefenseLeader(baseUser.getId());
    }

    teacher.setIsDefenseLeader(isDefenseLeader);
    teacher.setGroupId(groupId);

    if (isDefenseLeader) {
      // 如果是答辩组长，创建DefenseLeader对象
      DefenseLeader defenseLeader = EntityHelper.buildDefenseLeader(teacher, year);
      defenseLeader.setGroupId(groupId);
      userType = "defenseLeader";
      return new LoginResponse(userType, defenseLeader);
    } else {
      userType = "teacher";
      return new LoginResponse(userType, teacher);
    }
  }

  @Override
  public List<Integer> getTeacherDefenseYears(String teacherId) {
    // 1. 验证教师编号格式
    if (teacherId == null || teacherId.trim().isEmpty()) {
      throw new IllegalArgumentException("教师编号不能为空");
    }

    // 2. 检查教师是否存在且是教师角色
    if (!checkTeacherExists(teacherId)) {
      throw new IllegalArgumentException("该教师不存在或不是教师角色");
    }

    // 3. 从数据库查询该教师指导过的年份
    List<Integer> years = loginMapper.findDefenseYearsByTeacherId(teacherId);

    // 4. 打印调试信息
    System.out.println("查询教师 " + teacherId + " 的年份数据: " + years);

    // 5. 如果没有找到年份，检查是否在 dbgroup 表中作为答辩组长
    if (CollectionUtils.isEmpty(years)) {
      // 检查教师是否在任何年份担任过答辩组长
      List<Integer> leaderYears = findDefenseLeaderYears(teacherId);
      System.out.println("从答辩组查询到的年份: " + leaderYears);

      if (CollectionUtils.isNotEmpty(leaderYears)) {
        years = leaderYears;
      }
    }

    // 6. 去重并排序（降序）
    if (CollectionUtils.isNotEmpty(years)) {
      return years.stream()
              .distinct()
              .sorted((a, b) -> b - a)
              .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }
  private List<Integer> findDefenseLeaderYears(String teacherId) {
    // 这里需要添加一个查询方法到 LoginMapper 中
    // 查询 dbgroup 表中教师作为组长的年份
    // 暂时使用现有的方法，通过循环检查
    List<Integer> years = new ArrayList<>();

    // 检查近几年的年份（例如2020-2030）
    for (int year = 2020; year <= 2030; year++) {
      Boolean isLeader = loginMapper.checkIsDefenseLeaderByYear(teacherId, year);
      if (Boolean.TRUE.equals(isLeader)) {
        years.add(year);
      }
    }

    return years;
  }


  private void copyBaseUserProperties(BaseUser target, BaseUser source) {
    target.setId(source.getId());
    target.setPwd(source.getPwd());
    target.setRole(source.getRole());
    target.setRealName(source.getRealName());
  }
}
