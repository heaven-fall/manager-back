package com.world.back.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper
{
  @Select("select count(1) from user where role=1")
  Long getAdminCount();
  
  @Select("select count(1) from user where role=2")
  Long getTeacherCount();
}
