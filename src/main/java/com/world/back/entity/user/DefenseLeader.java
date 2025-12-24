package com.world.back.entity.user;

import com.world.back.entity.Student;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class DefenseLeader extends Teacher {
    private Integer groupId;
    private Integer year;
    private Integer guidedStudentsCount;

    private List<Teacher> groupTeachers;

    private List<Student> groupStudents;
}