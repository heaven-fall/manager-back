package com.world.back.entity;

import lombok.Data;

@Data
public class InstAdmin {
    private Integer adminId;
    private String adminName;
    private String institute;  // 院系名
    private String pwd;
    private String role;
    private String realName;
}
