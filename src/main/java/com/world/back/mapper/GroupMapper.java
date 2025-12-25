package com.world.back.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface GroupMapper
{
    @Select("select * from dbgroup where year=#{year} and admin_id!='admin'")
    List<Map<String, Object>> getAllGroups(Integer year);
    
    @Insert("insert into dbgroup(admin_id, year, student_count, max_student_count) values(#{admin_id},#{year},0,#{max_student_count})")
    void createGroup(String admin_id, int year, int max_student_count);
}
