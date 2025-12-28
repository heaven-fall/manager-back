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
    
    @PostMapping("/yeardelete")
    public Result<Boolean> yearDelete(@RequestBody Map<String, Object> map)
    {
        Integer year = (Integer) map.get("year");
        defenseService.yearDelete(year);
        return Result.success(true);
    }
    
    @GetMapping("/allyear")
    public Result<List<Map<String, Object>>> yearAll()
    {
        List<Map<String, Object>> res = defenseService.yearAll();
        for (Map<String, Object> map : res) {
            map.put("groupCount", defenseService.getCountByYear((Integer)map.get("year")));
            map.put("defenseCount", defenseService.getStudentCountByYear((Integer)map.get("year")));
        }
        return Result.success(res);
    }
    @PostMapping("/save-score")
    public Result<Boolean> saveScore(@RequestBody Map<String, Object> map)
    {

        return Result.success(defenseService.saveScore(map));
    }
}
