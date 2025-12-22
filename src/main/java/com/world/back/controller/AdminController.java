package com.world.back.controller;

import com.world.back.entity.res.Result;
import com.world.back.entity.user.Admin;
import com.world.back.entity.user.InstituteAdmin;
import com.world.back.serviceImpl.InstituteServiceImpl;
import com.world.back.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private InstituteServiceImpl instituteService;
    @PostMapping("/create")
    public Result<String> createInstituteAdmin(@RequestBody Map<String, Object> data) {
        String realName = data.get("realName").toString();
        String institute = data.get("institute").toString();
        String username = data.get("username").toString();
        String password = data.get("password").toString();
        String phone = data.get("phone").toString();
        String email = data.get("email").toString();
        InstituteAdmin instituteAdmin = new InstituteAdmin();
        instituteAdmin.setRealName(realName);
        instituteAdmin.setInstId(Integer.parseInt(institute));
        instituteAdmin.setPwd(password);
        instituteAdmin.setRole(1);
        instituteAdmin.setPhone(phone);
        instituteAdmin.setEmail(email);
        instituteAdmin.setId(username);
        userService.createAdmin(instituteAdmin);
        return Result.success("创建成功");
    }

    @GetMapping("/list")
    public Result<List<Admin>> getInstituteAdmins() {
        List<Admin> admins = userService.getAllAdmins();
        for (Admin admin : admins) {
            admin.setPwd(instituteService.getInstituteNameById(admin.getInstId()));
        }
        return Result.success(admins);
    }
    
    @PostMapping("/update")
    public Result<Boolean> updateInstituteAdmin(@RequestBody Map<String, Object> data)
    {
        String realName=data.get("realName").toString();
        String username = data.get("id").toString();
        String phone = data.get("phone").toString();
        String email = data.get("email").toString();
        return Result.success(userService.updateAdmin(realName, username, phone, email));
    }
    
    @GetMapping("/count")
    public Result<Long> getCount()
    {
        return Result.success(userService.getAdminCount());
    }
}
