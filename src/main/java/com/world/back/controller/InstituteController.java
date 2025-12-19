package com.world.back.controller;

import com.world.back.entity.Institute;
import com.world.back.entity.res.Result;
import com.world.back.serviceImpl.InstituteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/institute")
public class InstituteController {

    @Autowired
    private InstituteServiceImpl instituteService;
    @GetMapping("/list")
    public Result<List<Institute>> getInstituteList() {
        // 简单实现，返回模拟数据
        List<Institute> list = new ArrayList<>();
        list.add(new Institute(1, "计算机学院"));
        list.add(new Institute(2, "数学学院"));
        return Result.success(list);
    }

    @GetMapping("/all")
    public Result<List<Institute>> getAllInstitutes() {
        // 同上
        return Result.success(new ArrayList<>());
    }
    
    @GetMapping("/count")
    public Result<Long> getInstituteCount()
    {
        return Result.success(instituteService.getInstituteCount());
    }
}
