package com.world.back.mapper;

import com.world.back.entity.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {
    
    @Select("select * from student where id = #{id}")
    Student findById(@Param("id") String id);

    // 查询学院下的学生列表（支持分页和搜索）
    @Select("select * from student where institute_id=#{instituteId}")
    List<Map<String, Object>> findListByInstitute(
            @Param("instituteId") Long instituteId
    );
    
    @Select("select * from student where institute_id=#{instituteId} limit #{pagesize} offset #{offset}")
    List<Map<String, Object>> findListByInstitutePage(Integer instituteId, Integer offset, Integer pagesize);

    // 插入学生
    @Insert("insert into student(id, real_name, tel, email, institute_id) " +
            "values(#{id}, #{realName}, #{tel}, #{email}, #{instituteId})")
    int insert(Student student);

    // 更新学生
    @Update({
            "<script>",
            "update student set",
            "real_name = #{realName},",
            "<if test='tel != null'>tel = #{tel},</if>",
            "<if test='email != null'>email = #{email},</if>",
            "institute_id = #{instituteId}",
            "where id = #{id}",
            "</script>"
    })
    int update(Student student);

    // 删除学生
    @Delete("delete from student where id = #{id}")
    int deleteById(@Param("id") String id);

    // 分配答辩小组（通过dbinfo表关联）
    @Insert("insert into dbinfo(gid, stu_id, type) values(#{groupId}, #{studentId}, #{type}) " +
            "on duplicate key update gid = #{groupId}")
    int assignGroup(@Param("studentId") String studentId, @Param("groupId") Integer groupId, Integer type);

    // 检查学号是否存在
    @Select("select count(*) from student where id = #{studentId}")
    int checkStudentIdExists(@Param("studentId") String studentId);

    // 根据答辩小组查询学生
    @Select("select s.* from student s " +
            "join dbinfo d on s.id = d.stu_id " +
            "where d.gid = #{groupId}")
    List<Student> findStudentsByGroupId(@Param("groupId") Long groupId);

    @Select("select gid from dbinfo where stu_id=#{id}")
    Integer findGroupIdByStudentId(@Param("id") String id);

    // 移除学生答辩小组分配
    @Delete("delete from dbinfo where stu_id = #{studentId}")
    int removeGroupAssignment(@Param("studentId") String studentId);

    // 获取学生所属答辩小组信息
    @Select("select g.*, u.real_name as admin_name from dbgroup g " +
            "left join dbinfo d on g.id = d.gid " +
            "left join user u on g.admin_id = u.id " +
            "where d.stu_id = #{studentId}")
    Map<String, Object> getStudentGroupInfo(@Param("studentId") String studentId);

    // 获取学生答辩信息（从dbinfo表）
    @Select("select d.* from dbinfo d where d.stu_id = #{studentId}")
    Map<String, Object> getStudentDefenseInfo(@Param("studentId") String studentId);

    @Select("select * from student where institute_id=#{instituteId}")
    List<Student> getStudentByInstituteId(Integer instituteId);
    
    @Select("select * from dbinfo where stu_id=#{id} limit 1")
    Map<String, Object> getDbInfoById(String id);
    
    @Select("select tea_id from tea_stu_rel where stu_id=#{id}")
    String getTeacherById(String id);
    
    @Select("select gid from dbinfo where stu_id=#{sid}")
    Integer getGidBySid(String sid);
    
    @Select("select count(1) from student where institute_id=#{instituteId}")
    Integer getCount(Integer instituteId);
    
    @Select("select count(1) from student where institute_id=#{instituteId} and id not in (select stu_id from dbinfo)")
    Integer getUnassignCount(Integer instituteId);
}
