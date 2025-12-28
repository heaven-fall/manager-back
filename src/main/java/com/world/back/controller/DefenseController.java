package com.world.back.controller;

import com.world.back.entity.info.DefenseGroupInfo;
import com.world.back.entity.info.DefenseInfo;
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


    /**
     * 获取教师当前所在的答辩小组信息
     */
    @GetMapping("/current-group")
    public Result<DefenseGroupInfo> getCurrentGroup(@RequestParam String teacherId) {
        DefenseGroupInfo groupInfo = defenseService.getCurrentGroup(teacherId);
        return Result.success(groupInfo);
    }

    /**
     * 获取答辩小组的学生列表
     */
    @GetMapping("/group-students")
    public Result<List<DefenseInfo>> getGroupStudents(
            @RequestParam Integer groupId,
            @RequestParam String teacherId) {
        try {
            List<DefenseInfo> students = defenseService.getGroupStudents(groupId, teacherId);
            return Result.success(students);
        } catch (Exception e) {
            return Result.error("获取学生列表失败: " + e.getMessage());
        }
    }

    /**
     * 为答辩组长获取更详细的学生信息
     */
    @GetMapping("/group-students/leader")
    public Result<List<DefenseInfo>> getGroupStudentsForLeader(
            @RequestParam Integer groupId) {
        try {
            List<DefenseInfo> students = defenseService.getGroupStudentsForLeader(groupId);
            return Result.success(students);
        } catch (Exception e) {
            return Result.error("获取学生列表失败: " + e.getMessage());
        }
    }
}
