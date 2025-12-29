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
    
    @Select("select id from dbgroup where year=#{year} and admin_id=#{admin_id}")
    Integer getGidByYearId(Integer year, String admin_id);

    @Update("update dbgroup set admin_id=#{admin_id},max_student_count=#{max_student_count} where id=#{id}")
    void updateGroup(Integer id, String admin_id, int max_student_count);

    @Delete("delete from dbinfo where gid=#{id}")
    void deleteGroupInfo(Integer id);
    
    @Delete("delete from tea_group_rel where group_id=#{id}")
    void deleteGroupRelation(Integer id);

    @Delete("delete from tea_group_rel where group_id=#{gid} and is_defense_leader!=0")
    void deleteAdmin(Integer gid);

    @Delete("delete from dbgroup where id=#{id}")
    void deleteGroup(Integer id);

    @Select("select max_student_count from dbgroup where id=#{gid}")
    int getMaxStudentCountByGid(Integer gid);
    
    @Select("select * from dbinfo where gid=#{id}")
    List<Map<String, Object>> getMember(Integer id);
    
    @Delete("delete from dbinfo where gid=#{group_id} and stu_id=#{student_id}")
    void deleteFromGroup(Integer group_id, String student_id);
}
