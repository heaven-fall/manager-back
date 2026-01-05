package com.world.back.mapper;

import com.world.back.entity.Institute;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InstituteMapper
{
  @Select("select count(1) from institute")
  Long getInstituteCount();

  @Select("select id, name, user_id as adminId from institute")
  List<Institute> getAll();

  @Select("select count(1) from user_inst_rel where inst_id=#{id}")
  Integer getTeacherCount(int id);

  @Select("select count(1) from student where institute_id=#{id}")
  Integer getStudentCount(int id);

  @Select("select name from institute where id=#{id}")
  String getInstituteNameById(Integer id);

  @Insert("insert into institute(name, user_id) values(#{name}, #{adminId})")
  void addInstitute(@Param("name") String name, @Param("adminId") String adminId);

  @Update("update institute set name=#{name}, user_id=#{adminId} where id=#{id}")
  Boolean updateInstitute(Institute institute);

  @Delete("delete from institute where id=#{id}")
  void deleteInstitute(Integer id);

  @Select("select count(1) from institute where name = #{name}")
  int checkInstituteNameExists(String name);
  // 获取管理员管理的所有院系
  @Select("SELECT * FROM institute WHERE user_id = #{adminId}")
  List<Institute> getInstitutesByAdminId(String adminId);

  // 获取未安排管理员的院系
  @Select("SELECT id, name FROM institute WHERE user_id IS NULL OR user_id = ''")
  List<Institute> getAvailableInstitutes();
  @Delete("delete from user_inst_rel where user_id = #{userId} and inst_id = #{instituteId}")
  Boolean deleteUserInstRel(@Param("userId") String userId, @Param("instituteId") Integer instituteId);

  // 根据用户ID查找院系ID
  @Select("SELECT inst_id FROM user_inst_rel WHERE user_id = #{userId} LIMIT 1")
  List<Integer> findInstituteIdsByUserId(@Param("userId") String userId);

  // 根据院系ID查找院系名称
  @Select("SELECT name FROM institute WHERE id = #{id}")
  String findInstituteNameById(@Param("id") Integer id);

}