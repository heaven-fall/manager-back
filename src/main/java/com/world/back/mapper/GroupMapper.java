package com.world.back.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface GroupMapper
{
    @Select("select * from dbgroup where admin_id!='admin' and (year=#{year} or #{year}=0)")
    List<Map<String, Object>> getAllGroups(Integer year);
    
    @Insert("insert into dbgroup(admin_id, year, student_count, max_student_count) values(#{admin_id},#{year},0,#{max_student_count})")
    void createGroup(String admin_id, int year, int max_student_count);
    
    @Update("update dbgroup set admin_id=#{admin_id},max_student_count=#{max_student_count} where id=#{id}")
    void updateGroup(Integer id, String admin_id, int max_student_count);
    
    @Delete("delete from dbinfo where gid=#{id}")
    void beforeDeleteGroup(Integer id);
    
    @Delete("delete from dbgroup where id=#{id}")
    void deleteGroup(Integer id);
}
