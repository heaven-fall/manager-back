package com.world.back.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DefenseMapper
{
    @Insert("insert into dbgroup(admin_id,year) values('admin',#{year})")
    void yearAdd(Integer year);
    
    @Select("select * from dbgroup where admin_id='admin'")
    List<Map<String, Object>> yearAll();
}
