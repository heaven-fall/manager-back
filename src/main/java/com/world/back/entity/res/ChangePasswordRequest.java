package com.world.back.entity.res;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String userId;
    private String oldPassword;
    private String newPassword;
}