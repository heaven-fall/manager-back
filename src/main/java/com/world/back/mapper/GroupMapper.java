package com.world.back.mapper;

import com.world.back.entity.Student;
import com.world.back.entity.res.Group;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface GroupMapper
{
    @Select("select * from dbgroup where admin_id!='admin' and (year=#{year} or #{year}=0)")
    List<Map<String, Object>> getAllGroups(Integer year);

    @Insert("insert into dbgroup(admin_id, year, max_student_count) values(#{admin_id},#{year},#{max_student_count})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void createGroup(Group group);

    @Update("update dbgroup set admin_id=#{admin_id},max_student_count=#{max_student_count} where id=#{id}")
    void updateGroup(Integer id, String admin_id, int max_student_count);

    @Delete("delete from dbinfo where gid=#{id}")
    void beforeDeleteGroup(Integer id);

    @Delete("delete from tea_group_rel where group_id=#{gid} and is_defense_leader!=0")
    void deleteAdmin(Integer gid);

    @Delete("delete from dbgroup where id=#{id}")
    void deleteGroup(Integer id);

    @Select("select * from student join dbinfo on id=stu_id where gid=#{gid}")
    List<Student> getStudentByGid(Integer gid);

    @Select("select max_student_count from dbgroup where id=#{gid}")
    int getMaxStudentCountByGid(Integer gid);
}
