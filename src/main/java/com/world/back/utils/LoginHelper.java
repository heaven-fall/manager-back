package com.world.back.utils;

import com.world.back.entity.user.BaseUser;

public class LoginHelper {

    // 根据角色获取用户类型字符串
    public static String getUserTypeByRole(Integer role) {
        switch (role) {
            case 0: return "admin";
            case 1: return "instAdmin";
            case 2: return "teacher";
            default: return "unknown";
        }
    }

    // 验证用户角色
    public static boolean validateUserRole(BaseUser user, Integer expectedRole) {
        return user != null && user.getRole() != null && user.getRole().equals(expectedRole);
    }
}