package com.world.back.entity;

import lombok.Data;

@Data
public class Admin {
    private Integer adminId;
    private String adminName;
    private String pwd;
    private String role;
    private String realName;
}

