package com.world.back.entity;

import lombok.Data;

@Data
public class LoginResponse {
    private String userType;  // admin, instAdmin, defenseLeader, teacher
    private Object userInfo;  // 对应的用户对象

    public LoginResponse(String userType, Object userInfo) {
        this.userType = userType;
        this.userInfo = userInfo;
    }
}
