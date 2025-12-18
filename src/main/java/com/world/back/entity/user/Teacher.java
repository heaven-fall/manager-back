package com.world.back.entity.user;

import com.world.back.entity.Student;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Teacher extends BaseUser {
    private Integer instituteId;
    private String instituteName;

    private Boolean isDefenseLeader = false;

    private Integer guidedStudentsCount;

    private List<Student> currentYearGuidedStudents;

}