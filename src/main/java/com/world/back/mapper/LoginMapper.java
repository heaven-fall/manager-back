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
  @Select("SELECT admin_id, admin_name, pwd, role FROM admin " +
          "WHERE admin_name = #{adminName} AND pwd = #{password}")
  Admin adminLogin(@Param("adminName") String adminName,
                          @Param("password") String password);

  @Select("SELECT admin_id, admin_name, institute, pwd, role FROM inst_admin " +
          "WHERE admin_name = #{adminName} AND pwd = #{password}")
  InstAdmin instAdminLogin(@Param("adminName") String adminName,
                                  @Param("password") String password);

  @Select("SELECT teacher_id, teacher_name, institute, pwd, role FROM teacher " +
          "WHERE teacher_id = #{teacherId} AND pwd = #{password} AND role='teacher'")
  Teacher teacherLogin(@Param("teacherId") Integer teacherId,
                       @Param("password") String password);

  @Select("SELECT teacher_id, teacher_name, institute, pwd, role FROM teacher " +
          "WHERE teacher_id = #{teacherId} AND pwd = #{password} AND role='defenseLeader'")
  DefenseLeader defenseLeaderLogin(@Param("teacherId") Integer teacherId,
                             @Param("password") String password);
}
