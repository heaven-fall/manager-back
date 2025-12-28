package com.world.back.mapper;

import com.world.back.entity.user.Teacher;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface TeacherMapper {

    // 查询教师列表（包括院系信息）
    @Select("select * from user inner join user_inst_rel on user_id=id where inst_id=#{instituteId} or #{instituteId}=0")
    List<Teacher> selectTeacherList(
            @Param("instituteId") Integer instituteId);

    // 查询教师的小组信息 - 修复后的SQL
    @Select("""
    SELECT
        dg.id as groupId,
        dg.year as groupYear,
        CASE
            WHEN tgr.is_defense_leader = true THEN true
            WHEN tgr.is_defense_leader = 1 THEN true
            ELSE false
        END as isDefenseLeader
    FROM tea_group_rel tgr
    JOIN dbgroup dg ON tgr.group_id = dg.id
    WHERE tgr.teacher_id = #{teacherId}
    """)
    @Results({
            @Result(property = "groupId", column = "groupId"),
            @Result(property = "groupYear", column = "groupYear"),
            @Result(property = "isDefenseLeader", column = "isDefenseLeader")
    })
    List<Teacher.GroupInfo> selectTeacherGroups(@Param("teacherId") String teacherId);

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
    select
        u.id,
        u.real_name as realName,
        u.phone,
        u.email,
        u.role,
        ui.inst_id as instituteId,
        i.name as instituteName,
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

    // 添加教师到小组
    @Insert("""
    insert into tea_group_rel (teacher_id, group_id, is_defense_leader)
    values (#{teacherId}, #{groupId}, #{isDefenseLeader})
    on duplicate key update is_defense_leader = #{isDefenseLeader}
    """)
    int insertTeacherGroupRelation(
            @Param("teacherId") String teacherId,
            @Param("groupId") Integer groupId,
            @Param("isDefenseLeader") Boolean isDefenseLeader
    );

    // 设置答辩组长
    @Update("""
    <script>
    update tea_group_rel
    set is_defense_leader = case
        when teacher_id = #{teacherId} then true
        else false
    end
    where group_id = #{groupId}
    </script>
    """)
    int setDefenseLeader(
            @Param("groupId") Integer groupId,
            @Param("teacherId") String teacherId
    );

    // 从小组中移除教师
    @Delete("delete from tea_group_rel where teacher_id = #{teacherId} and group_id = #{groupId}")
    int deleteTeacherGroupRelation(
            @Param("teacherId") String teacherId,
            @Param("groupId") Integer groupId
    );

    // 删除教师的所有小组关系
    @Delete("delete from tea_group_rel where teacher_id = #{teacherId}")
    int deleteAllTeacherGroupRelations(@Param("teacherId") String teacherId);

    // 查询可用的年份
    @Select("select distinct year from dbgroup order by year desc")
    List<Integer> selectAvailableYears();

    // 查询某年份下的小组
    @Select("select id from dbgroup where year = #{year} order by id")
    List<Integer> selectGroupsByYear(@Param("year") Integer year);

    // 检查教师是否已在某年份的小组中
    @Select("""
    select count(*)
    from tea_group_rel tgr
    join dbgroup dg on tgr.group_id = dg.id
    where tgr.teacher_id = #{teacherId} and dg.year = #{year}
    """)
    int checkTeacherInYear(@Param("teacherId") String teacherId, @Param("year") Integer year);

    // 检查小组是否存在
    @Select("select count(*) from dbgroup where id = #{groupId}")
    int checkGroupExists(@Param("groupId") Integer groupId);

    // 删除教师
    @Delete("delete from user where id = #{id}")
    int deleteTeacher(@Param("id") String id);

    // 删除院系关系
    @Delete("delete from user_inst_rel where user_id = #{userId}")
    int deleteUserInstituteRelation(@Param("userId") String userId);

    // 检查教师是否存在
    @Select("select count(*) from user where id = #{id}")
    int checkTeacherExists(@Param("id") String id);

    // 检查小组是否已有答辩组长
    @Select("""
    select count(*)
    from tea_group_rel
    where group_id = #{groupId}
    and is_defense_leader = true
    """)
    int checkDefenseLeaderExists(@Param("groupId") Integer groupId);

    // 获取小组的当前答辩组长
    @Select("""
    select teacher_id
    from tea_group_rel
    where group_id = #{groupId}
    and is_defense_leader!=0
    limit 1
    """)
    String getDefenseLeaderByGroupId(@Param("groupId") Integer groupId);

    // 获取小组年份
    @Select("select year from dbgroup where id = #{groupId}")
    Integer getGroupYearById(@Param("groupId") Integer groupId);

    // 清除组长的方法
    @Update("""
    update tea_group_rel
    set is_defense_leader = false
    where group_id = #{groupId}
    """)
    int clearDefenseLeader(@Param("groupId") Integer groupId);

    // 测试SQL：直接查询教师小组关系的原始数据
    @Select("""
    select
        teacher_id,
        group_id,
        is_defense_leader
    from tea_group_rel
    where teacher_id = #{teacherId}
    """)
    List<Map<String, Object>> selectRawTeacherGroups(@Param("teacherId") String teacherId);
}
