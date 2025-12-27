package com.world.back.mapper;

import com.world.back.entity.user.BaseUser;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface LoginMapper {

  @Select("select id, pwd, role, real_name as realName " +
          "from user " +
          "where id = #{username} and pwd = #{password}")
  BaseUser findBaseUser(@Param("username") String username,
                        @Param("password") String password);

  @Select("select inst_id from user_inst_rel where user_id = #{userId}")
  Integer findInstituteIdByUserId(@Param("userId") String userId);

  @Select("select name from institute where id = #{instituteId}")
  String findInstituteNameById(@Param("instituteId") Integer instituteId);

  @Select("select count(*) > 0 from dbgroup where admin_id = #{userId}")
  Boolean checkIsDefenseLeader(@Param("userId") String userId);

  @Select("select * from tea_group_rel t inner join dbgroup d on d.id=t.group_id where t.teacher_id=#{teacher_id}")
  List<Map<String, Object>> findDefenseYears(String teacher_id);

  // 添加方法：查询教师的所有权限年份（包括指导关系和答辩组长）
  @Select("select distinct year from tea_stu_rel where tea_id = #{teacherId} " +
          "union " +
          "select distinct year from dbgroup where admin_id = #{teacherId} " +
          "order by year desc")
  List<Integer> findAllDefenseYearsByTeacherId(@Param("teacherId") String teacherId);

  @Select("select id from dbgroup where admin_id = #{userId} and year = #{year} limit 1")
  Integer findGroupIdByLeaderAndYear(@Param("userId") String userId,
                                     @Param("year") Integer year);

  @Select("select id, pwd, role, real_name as realName from user where id = #{id}")
  BaseUser findUserById(@Param("id") String id);

  @Select("select count(*) > 0 from tea_stu_rel where tea_id = #{teacherId} and year = #{year}")
  Boolean checkTeacherDefenseYearPermission(@Param("teacherId") String teacherId,
                                            @Param("year") Integer year);

  @Select("select distinct year from tea_stu_rel where tea_id = #{teacherId} order by year desc")
  List<Integer> findDefenseYearsByTeacherId(@Param("teacherId") String teacherId);
}
