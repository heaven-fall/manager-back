package com.world.back.mapper;

import com.world.back.entity.Student;
import org.apache.ibatis.annotations.Delete;
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

    @Delete("delete from dbgroup where year=#{year} and admin_id='admin'")
    void yearDelete(Integer year);

    @Select("select * from dbgroup where admin_id='admin'")
    List<Map<String, Object>> yearAll();

    @Select("select count(1) from dbgroup where year=#{year} and admin_id!='admin'")
    Integer getCountByYear(Integer year);

    @Select("select count(1) from dbgroup inner join dbinfo on id=gid where year=#{year} and admin_id!='admin'")
    Integer getStudentCountByYear(Integer year);

    @Select("select * from student where institute_id=#{instituteId}")
    List<Student> getStudentByInstituteId(Integer instituteId);
}
