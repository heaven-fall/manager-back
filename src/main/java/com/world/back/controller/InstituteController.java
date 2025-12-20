package com.world.back.controller;

import com.world.back.entity.Institute;
import com.world.back.entity.res.Result;
import com.world.back.serviceImpl.InstituteServiceImpl;
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
}
