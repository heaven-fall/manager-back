package com.world.back.entity.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InstituteAdmin extends BaseUser {
    private Integer instituteId;
    private String instituteName;
    // 动态属性
    private Integer managedTeachersCount;
    private Integer managedStudentsCount;
}
