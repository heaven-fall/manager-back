package com.world.back.mapper;

import com.world.back.entity.user.Admin;
import com.world.back.entity.user.BaseUser;
import com.world.back.entity.user.InstituteAdmin;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper
{
  @Select("select count(1) from user where role=1")
  Long getAdminCount();

  @Select("select real_name from user where id=#{id}")
  String getRealNameById(String id);

  @Select("select id,role,real_name,inst_id,phone,email from user left join user_inst_rel on user_id=id where role=1")
  List<Admin> getAllAdmins();

  @Insert("insert into user(id, pwd, role, real_name) values(#{id},#{password},#{role},#{realName})")
  Boolean createAdmin(String id, String realName, int role, String password);

  @Insert("insert into user_inst_rel values(#{user_id}, #{inst_id})")
  Boolean createUserInstRel(String user_id, Integer inst_id);

  @Update("update user set real_name=#{realName},id=#{username},phone=#{phone},email=#{email} where id=#{username}")
  void updateAdmin(String realName, String username, String phone, String email);

  @Delete("delete from user_inst_rel where inst_id=#{inst_id}")
  Boolean deleteUserInstRel(Integer inst_id);

  @Select("select id, pwd, role, real_name, phone, email, signaturePath from user where id=#{id}")
  BaseUser getUserById(String userId);

  @Update("update user set pwd = #{newPassword} where id = #{userId}")
  int updatePassword(@Param("userId") String userId, @Param("newPassword") String newPassword);

  @Update("update user set signaturePath = #{signaturePath} where id = #{userId}")
  int updateSignaturePath(@Param("userId") String userId,
                          @Param("signaturePath") String signaturePath);
  
  @Select("select inst_id from user_inst_rel where user_id=#{id}")
  Integer getInstIdByUserId(String userId);
}
