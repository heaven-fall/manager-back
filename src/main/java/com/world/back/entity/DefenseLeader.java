package com.world.back.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DefenseLeader extends Teacher {
    private String grantedYear;  // 答辩组长授予的年份

}
