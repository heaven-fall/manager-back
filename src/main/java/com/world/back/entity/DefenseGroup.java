package com.world.back.entity;

import lombok.Data;

import java.util.List;

@Data
public class DefenseGroup {
    private Integer id;
    private String leaderId;
    private String leaderName;
    private Integer year;
    private Integer instituteId;

    private List<Student> students;

    private Integer studentCount;
    private Integer defenseType;
}
