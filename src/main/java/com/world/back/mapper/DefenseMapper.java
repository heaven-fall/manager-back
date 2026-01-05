package com.world.back.mapper;

import com.world.back.entity.Coefficient;
import com.world.back.entity.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface DefenseMapper
{
    @Insert("INSERT INTO dbgroup(admin_id, year) SELECT 'admin', #{year} FROM dual " +
            "WHERE NOT EXISTS (SELECT 1 FROM dbgroup WHERE admin_id = 'admin' AND year = #{year})")
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

    @Update("update dbinfo set teacher_scores = #{teacherScoresJson}, graded_by = #{teacherId}, total_score=#{totalScore} " +
            "where stu_id = #{stuId} and gid = #{groupId}")
    int saveScore(Map<String, Object> map);

    @Select("select teacher_scores from dbinfo where stu_id = #{stuId} and gid = #{groupId}")
    String selectTeacherScoresJson(@Param("stuId") String stuId,
                                   @Param("groupId") String groupId);

    @Select("""
    select
        g.id as groupId,
        g.year,
        d.stu_id as studentId,
        s.real_name as studentName,
        i.name as instituteName,
        d.total_score as groupScore,
        gd.major_score as majorScore,
        g.adjustmentCoefficient as adjustmentCoefficient
    from dbgroup g
    join dbinfo d on g.id = d.gid
    join student s on d.stu_id = s.id
    join institute i on s.institute_id = i.id
    left join group_defense gd on gd.group_id = g.id and gd.stu_id = d.stu_id
    where g.year = #{year}
      and d.total_score = (
        select max(d2.total_score)
        from dbinfo d2
        where d2.gid = g.id
      )
    order by g.id
    """)
    List<Map<String, Object>> getGroupFirstStudents(@Param("year") Integer year);

    @Update(""" 
    <script>
    update dbgroup 
    set adjustmentCoefficient = CASE id 
        <foreach collection="gd" item="item" separator=" ">
            when #{item.groupId} then #{item.adjustmentCoefficient}
        </foreach>
    end
    where id in 
    <foreach collection="gd" item="item" open="(" separator="," close=")">
        #{item.groupId}
    </foreach>
    </script>
    """)
    int saveSaveCoefficients(@Param("gd") List<Coefficient> coefficients);

    // 根据组ID获取调节系数
    @Select("""
            select round(avg(adjustmentCoefficient), 3) as avg_coefficient
            from dbgroup
            where id = #{groupId}
              and adjustmentCoefficient is not null
    """)
    Double getAdjustmentCoefficient(@Param("groupId") Integer groupId);


    @Insert("""
    insert into group_defense (group_id, stu_id, major_score)
    values (#{groupId}, #{studentId}, #{majorScore})
    on duplicate key update
      major_score = #{majorScore}
    """)
    int saveMajorScore(@Param("groupId") Integer groupId,
                       @Param("studentId") String studentId,
                       @Param("majorScore") Double majorScore);
}
