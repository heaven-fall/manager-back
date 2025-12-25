package com.world.back.controller;

import com.world.back.entity.res.Result;
import com.world.back.serviceImpl.DefenseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/defense")
public class DefenseController
{
    @Autowired
    private DefenseServiceImpl defenseService;
    @PostMapping("/yearadd")
    public Result<Boolean> yearAdd(@RequestBody Map<String, Object> map)
    {
        Integer year = (Integer) map.get("year");
        defenseService.yearAdd(year);
        return Result.success(true);
    }
    
    @GetMapping("/allyear")
    public Result<List<Map<String, Object>>> yearAll()
    {
        return Result.success(defenseService.yearAll());
    }
}
