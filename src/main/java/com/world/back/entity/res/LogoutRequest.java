package com.world.back.entity.res;

import lombok.Data;

@Data
public class LogoutRequest {
    private String userId;
    private String token; // 可选字段
}