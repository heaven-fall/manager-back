package com.world.back.controller;

import com.world.back.entity.res.Result;
import com.world.back.serviceImpl.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private UserServiceImpl userService;
    @PostMapping("/create")
    public Result<String> createInstituteAdmin(@RequestBody Map<String, Object> data) {
        System.out.println("创建院系管理员: " + data);
        return Result.success("创建成功");
    }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getInstituteAdmins() {
        // 返回模拟数据
        List<Map<String, Object>> admins = new ArrayList<>();
        Map<String, Object> admin1 = new HashMap<>();
        admin1.put("id", 1);
        admin1.put("adminName", "张管理员");
        admin1.put("institute", "计算机学院");
        admins.add(admin1);
        return Result.success(admins);
    }
    
    @GetMapping("/count")
    public Result<Long> getCount()
    {
        return Result.success(userService.getAdminCount());
    }
}