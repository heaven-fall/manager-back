package com.world.back.controller;

import com.world.back.entity.res.Group;
import com.world.back.entity.res.Result;
import com.world.back.serviceImpl.GroupServiceImpl;
import com.world.back.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groups")
public class GroupController
{
    @Autowired
    private GroupServiceImpl groupService;
    @Autowired
    private UserServiceImpl userService;
    @GetMapping("/all")
    public Result<List<Map<String, Object>>> getAllGroups(@RequestParam Integer year)
    {
        List<Map<String, Object>> res = groupService.getAllGroups(year);
        for (Map<String, Object> map : res) {
            map.put("realName", userService.getNameById((String)map.get("admin_id")));
        }
        return Result.success(res);
    }
    
    @PostMapping("/create")
    public Result<Boolean> createGroup(@RequestBody Map<String, Object> map)
    {
        Group group = new Group();
        group.setAdmin_id(map.get("adminId").toString());
        group.setYear(Integer.parseInt(map.get("year").toString()));
        group.setMax_student_count(Integer.parseInt(map.get("maxStudents").toString()));
        groupService.createGroup(group);
        return Result.success(true);
    }
}
