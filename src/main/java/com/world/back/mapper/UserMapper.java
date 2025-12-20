package com.world.back.mapper;

import com.world.back.entity.user.BaseUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper
{
  @Select("select count(1) from user where role=1")
  Long getAdminCount();
  
  @Select("select count(1) from user where role=2")
  Long getTeacherCount();
  
  @Select("select id, real_name from user where role=2")
  List<BaseUser> getAllTeachers();
  
  @Select("select real_name from user where id=#{id}")
  String getRealNameById(String id);
}
