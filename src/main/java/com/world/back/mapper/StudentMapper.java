package com.world.back.mapper;

import com.world.back.entity.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {

    // 根据ID查询学生
    @Select("SELECT * FROM student WHERE id = #{id}")
    Student findById(@Param("id") String id);

    // 根据学号查询
    @Select("SELECT * FROM student WHERE id = #{studentId}")
    Student findByStudentId(@Param("studentId") String studentId);

    // 查询学院下的学生列表（支持分页和搜索）
    @Select({
            "<script>",
            "SELECT s.*, ",
            "g.year as group_year, g.status as group_status, ",
            "u.real_name as admin_name ",
            "FROM student s",
            "LEFT JOIN dbinfo d ON s.id = d.stu_id",
            "LEFT JOIN dbgroup g ON d.gid = g.id",
            "LEFT JOIN user u ON g.admin_id = u.id",
            "WHERE s.institute_id = #{instituteId}",
            "<if test='search != null and search != \"\"'>",
            "AND (s.real_name LIKE CONCAT('%', #{search}, '%')",
            "OR s.id LIKE CONCAT('%', #{search}, '%'))",
            "</if>",
            "ORDER BY s.id DESC",
            "LIMIT #{offset}, #{pageSize}",
            "</script>"
    })
    List<Map<String, Object>> findListByInstitute(
            @Param("instituteId") Long instituteId,
            @Param("search") String search,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize
    );

    // 查询总数
    @Select({
            "<script>",
            "SELECT COUNT(*) FROM student",
            "WHERE institute_id = #{instituteId}",
            "<if test='search != null and search != \"\"'>",
            "AND (real_name LIKE CONCAT('%', #{search}, '%')",
            "OR id LIKE CONCAT('%', #{search}, '%'))",
            "</if>",
            "</script>"
    })
    Integer countByInstitute(
            @Param("instituteId") Long instituteId,
            @Param("search") String search
    );

    // 插入学生
    @Insert("INSERT INTO student(id, real_name, tel, email, institute_id) " +
            "VALUES(#{id}, #{realName}, #{tel}, #{email}, #{instituteId})")
    int insert(Student student);

    // 更新学生
    @Update({
            "<script>",
            "UPDATE student SET",
            "real_name = #{realName},",
            "<if test='tel != null'>tel = #{tel},</if>",
            "<if test='email != null'>email = #{email},</if>",
            "institute_id = #{instituteId}",
            "WHERE id = #{id}",
            "</script>"
    })
    int update(Student student);

    // 删除学生
    @Delete("DELETE FROM student WHERE id = #{id}")
    int deleteById(@Param("id") String id);

    // 分配答辩小组（通过dbinfo表关联）
    @Insert("INSERT INTO dbinfo(gid, stu_id) VALUES(#{groupId}, #{studentId}) " +
            "ON DUPLICATE KEY UPDATE gid = #{groupId}")
    int assignGroup(@Param("studentId") String studentId, @Param("groupId") Long groupId);

    // 检查学号是否存在
    @Select("SELECT COUNT(*) FROM student WHERE id = #{studentId}")
    int checkStudentIdExists(@Param("studentId") String studentId);

    // 根据答辩小组查询学生
    @Select("SELECT s.* FROM student s " +
            "JOIN dbinfo d ON s.id = d.stu_id " +
            "WHERE d.gid = #{groupId}")
    List<Student> findStudentsByGroupId(@Param("groupId") Long groupId);

    // 移除学生答辩小组分配
    @Delete("DELETE FROM dbinfo WHERE stu_id = #{studentId}")
    int removeGroupAssignment(@Param("studentId") String studentId);

    // 获取学生所属答辩小组信息
    @Select("SELECT g.*, u.real_name as admin_name FROM dbgroup g " +
            "LEFT JOIN dbinfo d ON g.id = d.gid " +
            "LEFT JOIN user u ON g.admin_id = u.id " +
            "WHERE d.stu_id = #{studentId}")
    Map<String, Object> getStudentGroupInfo(@Param("studentId") String studentId);

    // 获取学生答辩信息（从dbinfo表）
    @Select("SELECT d.* FROM dbinfo d WHERE d.stu_id = #{studentId}")
    Map<String, Object> getStudentDefenseInfo(@Param("studentId") String studentId);
}