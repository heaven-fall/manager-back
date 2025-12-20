package com.world.back.mapper;

import com.world.back.entity.Institute;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InstituteMapper
{
  @Select("select count(1) from institute")
  Long getInstituteCount();
  
  @Select("select * from institute")
  List<Institute> getAll();
  @Select("select count(1) from user_inst_rel where inst_id=#{id}")
  Integer getTeacherCount(int id);
  @Select("select count(1) from student where institute_id=#{id}")
  Integer getStudentCount(int id);
  
  @Insert("insert into institute(name, user_id) values(#{name}, #{dean})")
  void addInstitute(String name, String dean);
}
