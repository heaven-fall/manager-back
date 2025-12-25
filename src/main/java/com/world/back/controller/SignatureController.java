package com.world.back.controller;

import com.world.back.entity.res.Result;
import com.world.back.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/signature")
@CrossOrigin
public class SignatureController {

    @Autowired
    private UserService userServiceImpl;

    // 获取当前签名
    @GetMapping("/current")
    public Result<String> getCurrentSignature(@RequestParam String userId) {
        return userServiceImpl.getCurrentSignature(userId);
    }

    // 上传签名
    @PostMapping("/upload")
    public Result<String> uploadSignature(@RequestParam("file") MultipartFile file,
                                          @RequestParam String userId) {
        return userServiceImpl.uploadSignature(file, userId);
    }
}