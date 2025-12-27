package com.world.back.utils;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthUtil {

    public static String getUserIdFromToken(HttpServletRequest request) {
        // 实现实际的Token解析逻辑
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 这里应该解析JWT token，暂时返回模拟的用户ID
        return "user_" + (token != null ? token.hashCode() : "anonymous");
    }
}
