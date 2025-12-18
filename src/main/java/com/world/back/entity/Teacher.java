package com.world.back.entity;

import lombok.Data;

@Data
public class Teacher {
    private Integer teacherId;
    private String teacherName;
    private String institute;
    private String pwd;
    private String role;  // 用于角色的标识（teacher、defenseLeader）
    private String realName;
}
