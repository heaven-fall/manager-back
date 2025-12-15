package com.world.back.mapper;

import com.world.back.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LoginMapper
{
  @Select("select * from user where username=#{username}")
  User login(String username, String password);
}
