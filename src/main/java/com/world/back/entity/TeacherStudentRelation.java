package com.world.back.entity;

import com.world.back.entity.user.Teacher;
import lombok.Data;

@Data
public class TeacherStudentRelation {
    private String teacherId;
    private String studentId;
    private Integer year;

    private Teacher teacher;
    private Student student;
}