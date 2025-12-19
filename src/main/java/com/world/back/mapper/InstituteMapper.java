package com.world.back.mapper;

import com.world.back.entity.res.Result;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface InstituteMapper
{
  @Select("select count(1) from institute")
  Long getInstituteCount();
}
