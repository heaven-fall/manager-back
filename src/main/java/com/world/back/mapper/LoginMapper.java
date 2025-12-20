package com.world.back.mapper;

import com.world.back.entity.user.BaseUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LoginMapper {

  @Select("SELECT id, pwd, role, real_name as realName " +
          "FROM user " +
          "WHERE id = #{username} AND pwd = #{password}")
  BaseUser findBaseUser(@Param("username") String username,
                        @Param("password") String password);

  @Select("SELECT inst_id FROM user_inst_rel WHERE user_id = #{userId}")
  Integer findInstituteIdByUserId(@Param("userId") String userId);

  @Select("SELECT name FROM institute WHERE id = #{instituteId}")
  String findInstituteNameById(@Param("instituteId") Integer instituteId);

  @Select("SELECT COUNT(*) > 0 FROM dbgroup WHERE user_id = #{userId}")
  Boolean checkIsDefenseLeader(@Param("userId") String userId);

  @Select("SELECT COUNT(*) > 0 FROM dbgroup WHERE user_id = #{userId} AND year = #{year}")
  Boolean checkIsDefenseLeaderByYear(@Param("userId") String userId,
                                     @Param("year") Integer year);

  // 添加方法：查询教师作为答辩组长的所有年份
  @Select("SELECT DISTINCT year FROM dbgroup WHERE user_id = #{userId} ORDER BY year DESC")
  List<Integer> findDefenseLeaderYears(@Param("userId") String userId);

  // 添加方法：查询教师的所有权限年份（包括指导关系和答辩组长）
  @Select("SELECT DISTINCT year FROM tea_stu_rel WHERE tea_id = #{teacherId} " +
          "UNION " +
          "SELECT DISTINCT year FROM dbgroup WHERE user_id = #{teacherId} " +
          "ORDER BY year DESC")
  List<Integer> findAllDefenseYearsByTeacherId(@Param("teacherId") String teacherId);
  // 可以添加更多查询方法
  @Select("SELECT id FROM dbgroup WHERE user_id = #{userId} AND year = #{year} LIMIT 1")
  Integer findGroupIdByLeaderAndYear(@Param("userId") String userId,
                                     @Param("year") Integer year);

  @Select("SELECT id, pwd, role, real_name as realName FROM user WHERE id = #{id}")
  BaseUser findUserById(@Param("id") String id);

  @Select("SELECT COUNT(*) > 0 FROM tea_stu_rel WHERE tea_id = #{teacherId} AND year = #{year}")
  Boolean checkTeacherDefenseYearPermission(@Param("teacherId") String teacherId,
                                            @Param("year") Integer year);

  @Select("SELECT DISTINCT year FROM tea_stu_rel WHERE tea_id = #{teacherId} ORDER BY year DESC")
  List<Integer> findDefenseYearsByTeacherId(@Param("teacherId") String teacherId);
}