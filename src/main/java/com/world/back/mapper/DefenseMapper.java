package com.world.back.mapper;

import com.world.back.entity.info.AdvisorInfo;
import com.world.back.entity.info.DefenseGroupInfo;
import com.world.back.entity.info.DefenseInfo;
import com.world.back.entity.Student;
import com.world.back.entity.info.StudentScores;
import org.apache.ibatis.annotations.*;

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

    // 查询答辩小组学生信息
    @Select("""
    SELECT
        s.id as studentId,
        s.real_name as studentName,
        s.tel as phone,
        s.email,
        s.institute_id as instituteId,
        i.name as instituteName,
        d.gid as defenseGroupId,
        d.title as thesisTitle,
        d.type as defenseType,
        d.time as defenseTime,
        d.summary,
        d.reviewer_id as reviewerId,
        ru.real_name as reviewerName,
        d.total_score as totalScore,
        d.comment,
        d.graded_by as gradedBy,
        d.paper_quality as paperQuality,
        d.presentation,
        d.qa_performance as qaPerformance,
        d.design_quality1 as designQuality1,
        d.design_quality2 as designQuality2,
        d.design_quality3 as designQuality3,
        d.design_presentation as designPresentation,
        d.design_qa1 as designQa1,
        d.design_qa2 as designQa2,
        d.teacher_scores as teacherScoresJson
    FROM dbinfo d
    JOIN student s ON d.stu_id = s.id
    JOIN institute i ON s.institute_id = i.id
    LEFT JOIN user ru ON d.reviewer_id = ru.id
    WHERE d.gid = #{groupId}
    """)
    @Results({
            @Result(property = "studentId", column = "studentId"),
            @Result(property = "studentName", column = "studentName"),
            @Result(property = "defenseGroupId", column = "defenseGroupId"),
            @Result(property = "thesisTitle", column = "thesisTitle"),
            @Result(property = "defenseType", column = "defenseType"),
            @Result(property = "teacherScores", column = "teacherScoresJson",
                    typeHandler = com.world.back.utils.JsonTypeHandler.class)
    })
    List<DefenseInfo> selectStudentsByGroupId(@Param("groupId") Integer groupId);

    // 查询学生成绩信息
    @Select("""
    SELECT
        stu_id as studentId,
        type,
        total_score as totalScore,
        comment,
        paper_quality as paperQuality,
        presentation,
        qa_performance as qaPerformance,
        design_quality1 as designQuality1,
        design_quality2 as designQuality2,
        design_quality3 as designQuality3,
        design_presentation as designPresentation,
        design_qa1 as designQa1,
        design_qa2 as designQa2,
        teacher_scores as teacherScoresJson,
        graded_by as gradedBy
    FROM dbinfo
    WHERE gid = #{groupId}
    """)
    List<StudentScores> selectScoresByGroupId(@Param("groupId") Integer groupId);

    // 查询指导教师信息
    @Select("""
    SELECT
        tsr.stu_id as studentId,
        tsr.tea_id as advisorId,
        u.real_name as advisorName,
        tsr.year as guidanceYear
    FROM tea_stu_rel tsr
    JOIN user u ON tsr.tea_id = u.id
    WHERE tsr.stu_id IN (
        SELECT stu_id FROM dbinfo WHERE gid = #{groupId}
    )
    AND tsr.year = (
        SELECT MAX(year) FROM tea_stu_rel tsr2
        WHERE tsr2.stu_id = tsr.stu_id
    )
    """)
    List<AdvisorInfo> selectAdvisorsByGroupId(@Param("groupId") Integer groupId);

    // 查询教师所在的小组信息
    @Select("""
    SELECT
        tgr.group_id as groupId,
        dg.year as year,
        dg.admin_id as adminId,
        dg.max_student_count as maxStudentCount,
        tgr.is_defense_leader as isDefenseLeader
    FROM tea_group_rel tgr
    JOIN dbgroup dg ON tgr.group_id = dg.id
    WHERE tgr.teacher_id = #{teacherId}
    AND dg.year = YEAR(CURDATE())
    LIMIT 1
    """)
    @Results({
            @Result(property = "groupId", column = "groupId"),
            @Result(property = "year", column = "year"),
            @Result(property = "adminId", column = "adminId"),
            @Result(property = "maxStudentCount", column = "maxStudentCount"),
            @Result(property = "isDefenseLeader", column = "isDefenseLeader")
    })
    DefenseGroupInfo selectCurrentGroup(@Param("teacherId") String teacherId);

    // 查询小组学生数量
    @Select("SELECT COUNT(*) FROM dbinfo WHERE gid = #{groupId}")
    Integer countStudentsByGroupId(@Param("groupId") Integer groupId);

    // 查询小组已评分学生数量
    @Select("SELECT COUNT(*) FROM dbinfo WHERE gid = #{groupId} AND total_score > 0")
    Integer countScoredStudentsByGroupId(@Param("groupId") Integer groupId);

    // 查询答辩组长姓名
    @Select("SELECT real_name FROM user WHERE id = #{adminId}")
    String selectAdminNameById(@Param("adminId") String adminId);

    // 为答辩组长获取学生的完整评分信息
    @Select("""
    SELECT
        d.stu_id as student_id,
        d.graded_by as teacher_id,
        u.real_name as teacher_name,
        d.total_score,
        d.paper_quality,
        d.presentation,
        d.qa_performance,
        d.design_quality1,
        d.design_quality2,
        d.design_quality3,
        d.design_presentation,
        d.design_qa1,
        d.design_qa2,
        d.comment
    FROM dbinfo d
    JOIN user u ON d.graded_by = u.id
    WHERE d.stu_id = #{studentId}
    AND d.gid = #{groupId}
    AND d.graded_by IS NOT NULL
    AND d.total_score > 0
    """)
    List<Map<String, Object>> selectAllTeacherScoresForStudent(
            @Param("studentId") String studentId,
            @Param("groupId") Integer groupId);
}
