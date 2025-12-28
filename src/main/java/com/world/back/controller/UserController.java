package com.world.back.controller;

import com.world.back.entity.res.ChangePasswordRequest;
import com.world.back.entity.res.Result;
import com.world.back.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/changePassword")
    public Result<String> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            boolean success = userService.changePassword(
                    request.getUserId(),
                    request.getOldPassword(),
                    request.getNewPassword()
            );

            if (success) {
                return Result.success();
            } else {
                return Result.error("原密码错误或用户不存在");
            }
        } catch (Exception e) {
            return Result.error("修改密码失败: " + e.getMessage());
        }
    }
}
