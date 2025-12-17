package com.world.back.mapper;

import com.world.back.entity.Admin;
import com.world.back.entity.DefenseLeader;
import com.world.back.entity.InstAdmin;
import com.world.back.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LoginMapper
{
  // admin登录
  @Select("select user_id as admin_id, real_name as admin_name, pwd, role " +
          "from user " +
          "where username = #{adminName} and password = #{password} and role = 'admin'")
  Admin adminLogin(@Param("adminName") String adminName,
                   @Param("password") String password);

  // 院系管理员登录
  @Select("select u.user_id as admin_id, u.real_name as admin_name, " +
          "u.pwd, u.role, ia.institute_id, i.institute_name as institute " +
          "from user u " +
          "join institute_admin ia on u.user_id = ia.admin_id " +
          "join institute i on ia.institute_id = i.institute_id " +
          "where u.username = #{adminName} and u.password = #{password} and u.role = 'institute_admin'")
  InstAdmin instAdminLogin(@Param("adminName") String adminName,
                           @Param("password") String password);

  // 教师登录
  @Select("select u.user_id as teacher_id, u.real_name as teacher_name, " +
          "u.pwd, u.role, t.institute_id, i.institute_name as institute " +
          "from user u " +
          "join teacher t on u.user_id = t.teacher_id " +
          "join institute i on t.institute_id = i.institute_id " +
          "where u.username = #{username} and u.password = #{password} and u.role = 'teacher'")
  Teacher teacherLogin(@Param("username") String username,
                       @Param("password") String password);

  // 答辩组长登录
  @Select("select u.user_id as teacher_id, u.real_name as teacher_name, " +
          "u.pwd, u.role, t.institute_id, i.institute_name as institute, " +
          "dl.granted_year " +
          "from user u " +
          "join defense_leader dl on u.user_id = dl.teacher_id " +
          "join teacher t on u.user_id = t.teacher_id " +
          "join institute i on t.institute_id = i.institute_id " +
          "where u.username = #{username} and u.password = #{password} and u.role = 'teacher'")
  DefenseLeader defenseLeaderLogin(@Param("username") String username,
                                   @Param("password") String password);
}