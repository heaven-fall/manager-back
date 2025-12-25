package com.world.back.mapper;

import com.world.back.entity.user.Teacher;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface TeacherMapper {

    @Select("select * from user_inst_rel inner join user on user_id=id where inst_id=#{instituteId} and role=2")
    List<Map<String, Object>> getTeacherListByInstituteId(@Param("instituteId") Integer instituteId);
    
    @Select("select * from user where role=2")
    List<Map<String, Object>> getTeacherList();
    // 查询教师总数
    @Select("""
        select count(*)
        from user u
        left join user_inst_rel ui on u.id = ui.user_id
        where u.role in (1, 2)
        and (#{instituteId} is null or ui.inst_id = #{instituteId})
        and (#{search} is null or u.real_name like concat('%', #{search}, '%') or u.id like concat('%', #{search}, '%'))
    """)
    Long countTeachers(
            @Param("instituteId") Integer instituteId,
            @Param("search") String search
    );

    // 根据ID查询教师
    @Select("""
        select u.id, u.real_name as name, u.phone, u.email, u.role,
               ui.inst_id as instituteId, i.name as instituteName,
               case when u.role = 1 then true else false end as isAdmin
        from user u
        left join user_inst_rel ui on u.id = ui.user_id
        left join institute i on ui.inst_id = i.id
        where u.id = #{id}
    """)
    Teacher selectTeacherById(@Param("id") String id);

    // 创建教师
    @Insert("""
    insert into user (id, real_name, pwd, role, phone, email)
    values (#{id}, #{realName}, #{pwd}, #{role}, #{phone}, #{email})
    """)
    int insertTeacher(Teacher teacher);

    // 插入院系关系
    @Insert("insert into user_inst_rel (user_id, inst_id) values (#{userId}, #{instituteId})")
    int insertUserInstituteRelation(
            @Param("userId") String userId,
            @Param("instituteId") Integer instituteId
    );

    // 更新教师信息
    @Update("""
        update user
        set real_name = #{name}, phone = #{phone}, email = #{email}
        where id = #{id}
    """)
    int updateTeacher(Teacher teacher);

    // 更新用户角色（是否管理员）
    @Update("update user set role = #{role} where id = #{id}")
    int updateTeacherRole(@Param("id") String id, @Param("role") Integer role);

    // 删除教师
    @Delete("delete from user where id = #{id}")
    int deleteTeacher(@Param("id") String id);

    // 删除院系关系
    @Delete("delete from user_inst_rel where user_id = #{userId}")
    int deleteUserInstituteRelation(@Param("userId") String userId);

    // 设置答辩组长
    @Update("update dbgroup set admin_id = #{teacherId} where id = #{groupId}")
    int setDefenseLeader(@Param("groupId") Integer groupId, @Param("teacherId") String teacherId);

    // 检查教师是否存在
    @Select("select count(*) from user where id = #{id}")
    int checkTeacherExists(@Param("id") String id);
}
