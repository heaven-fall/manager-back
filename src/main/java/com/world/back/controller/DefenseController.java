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

    @GetMapping("/first-students")
    public Result<List<Map<String, Object>>> getGroupFirstStudents(@RequestParam Integer year) {
        try {
            List<Map<String, Object>> students = defenseService.getGroupFirstStudents(year);
            return Result.success(students);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取数据失败: " + e.getMessage());
        }
    }

    @PostMapping("/save-major-score")
    public Result<Boolean> saveMajorScore(@RequestBody Map<String, Object> map) {
        try {
            Integer groupId = (Integer) map.get("groupId");
            String studentId = (String) map.get("studentId");
            Double majorScore = Double.parseDouble(map.get("majorScore").toString());

            Boolean result = defenseService.saveMajorScore(groupId, studentId, majorScore);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存大组成绩失败: " + e.getMessage());
        }
    }

    @PostMapping("/save-coefficients")
    public Result<Map<String, Object>> saveCoefficients(@RequestParam Integer year) {
        try {
            Map<String, Object> result = defenseService.SaveCoefficients(year);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("计算调节系数失败: " + e.getMessage());
        }
    }

    @GetMapping("/adjustment-coefficient")
    public Result<Double> getAdjustmentCoefficient(@RequestParam Integer groupId) {
        try {
            Double coefficient = defenseService.getAdjustmentCoefficient(groupId);
            return Result.success(coefficient);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取调节系数失败: " + e.getMessage());
        }
    }
}
