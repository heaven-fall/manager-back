package com.world.back.controller;

import com.world.back.entity.Student;
import com.world.back.entity.res.Group;
import com.world.back.entity.res.Result;
import com.world.back.service.InstituteService;
import com.world.back.service.StudentService;
import com.world.back.serviceImpl.DefenseServiceImpl;
import com.world.back.serviceImpl.GroupServiceImpl;
import com.world.back.serviceImpl.TeacherServiceImpl;
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
    private TeacherServiceImpl teacherService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private InstituteService instituteService;
    @Autowired
    private StudentService studentService;
    @GetMapping("/all")
    public Result<List<Map<String, Object>>> getAllGroups(@RequestParam Integer year)
    {
        List<Map<String, Object>> res = groupService.getAllGroups(year);
        for (Map<String, Object> map : res) {
            map.put("realName", userService.getNameById((String)map.get("admin_id")));
            map.put("student_count", groupService.getMember((Integer)map.get("id")).size());
        }
        return Result.success(res);
    }

    @PostMapping("/update")
    public Result<Boolean> updateGroup(@RequestBody Map<String, Object> map)
    {
        Integer group_id = (Integer)map.get("id");
        Group group = new Group();
        group.setAdmin_id(map.get("admin_id").toString());
        group.setYear(Integer.parseInt(map.get("year").toString()));
        group.setMax_student_count(Integer.parseInt(map.get("maxStudents").toString()));
        if (group_id != null)
        {
            groupService.deleteAdmin(group_id);
            groupService.updateGroup(group);
            teacherService.addTeacherToGroup(group.getAdmin_id(), group_id, true);
            return Result.success(true);
        }
        groupService.createGroup(group);
        teacherService.addTeacherToGroup(group.getAdmin_id(), group.getId(), true);
        return Result.success(true);
    }

    @PostMapping("/delete")
    public Result<Boolean> deleteGroup(@RequestBody Map<String, Object> map)
    {
        Integer group_id = (Integer)map.get("id");
        groupService.deleteGroup(group_id);
        return Result.success(true);
    }

    @GetMapping("/studentlist")
    public Result<List<Map<String, Object>>> getStudentList(@RequestParam Integer group_id)
    {
        List<Map<String, Object>> res = groupService.getMember(group_id);
        for (Map<String, Object> map : res) {
            map.put("instituteName", instituteService.getInstituteNameById((Integer) map.get("instituteId")));
            map.put("teacherName", userService.getNameById(studentService.getTeacherById((String) map.get("stu_id"))));;
        }
        return Result.success(res);
    }

    @GetMapping("/getmember")
    public Result<List<Map<String, Object>>> getMember(@RequestParam Integer group_id)
    {
        return Result.success(groupService.getMember(group_id));
    }
    
    @PostMapping("/deletefromgroup")
    public Result<Boolean> deleteFromGroup(@RequestBody Map<String, Object> map)
    {
        Integer group_id = (Integer)map.get("group_id");
        String student_id = (String)map.get("student_id");
        groupService.deleteFromGroup(group_id, student_id);
        return Result.success(true);
    }
}
