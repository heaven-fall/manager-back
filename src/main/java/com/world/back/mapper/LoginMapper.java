package com.world.back.mapper;

import com.world.back.entity.user.BaseUser;
import org.apache.ibatis.annotations.*;

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

  // 可以添加更多查询方法
  @Select("SELECT id FROM dbgroup WHERE user_id = #{userId} AND year = #{year} LIMIT 1")
  Integer findGroupIdByLeaderAndYear(@Param("userId") String userId,
                                     @Param("year") Integer year);
}