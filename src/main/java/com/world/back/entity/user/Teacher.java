package com.world.back.entity.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Teacher extends BaseUser {

    // 基础信息
    private String realName;

    // 院系信息
    private Integer instituteId;
    private String instituteName;

    // 小组信息
    private Integer groupId;
    private Integer groupYear;
    private Boolean isDefenseLeader = false;

    // 角色信息
    private Boolean isAdmin = false;
    private Integer role;

    // 其他信息
    private String title;
    private String department;
    private Integer guidedStudentsCount;



}