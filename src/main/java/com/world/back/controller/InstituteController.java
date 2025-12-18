package com.world.back.controller;

import com.world.back.entity.Institute;
import com.world.back.entity.res.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/institute")
public class InstituteController {

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
}
