package com.world.back.controller;

import com.world.back.entity.Institute;
import com.world.back.entity.res.Result;
import com.world.back.serviceImpl.InstituteServiceImpl;
import com.world.back.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/institute")
public class InstituteController {

    @Autowired
    private InstituteServiceImpl instituteService;
    @Autowired
    private UserServiceImpl userService;
    @GetMapping("/list")
    public Result<List<Institute>> getInstituteList() {
        List<Institute> list = instituteService.getAll();
        return Result.success(list);
    }
    
    @GetMapping("/count")
    public Result<Long> getInstituteCount()
    {
        return Result.success(instituteService.getInstituteCount());
    }
    
    @PostMapping("/add")
    public Result<Boolean> addInstitute(@RequestBody Map<String, Object> map)
    {
        String id=map.get("deanId").toString();
        String name=map.get("name").toString();
        Institute institute = new Institute(name, id);
        instituteService.addInstitute(institute);
        return Result.success(true);
    }
    
    @PostMapping("/update")
    public Result<Boolean> updateInstitute(@RequestBody Map<String, Object> map)
    {
        String id=map.get("id").toString();
        String name=map.get("name").toString();
        String adminId=map.get("adminId").toString();
        Institute institute = new Institute(name, adminId);
        institute.setId(Integer.parseInt(id));
        institute.setAdminId(map.get("adminId").toString());
        
        return Result.success(instituteService.updateInstitute(institute));
    }
    
    @PostMapping("/delete")
    public Result<Boolean> deleteInstitute(@RequestBody Map<String, Object> map)
    {
        return Result.success(instituteService.deleteInstitute(Integer.parseInt(map.get("id").toString())));
    }
}
