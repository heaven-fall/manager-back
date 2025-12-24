package com.world.back.controller;

import com.world.back.entity.res.Result;
import com.world.back.entity.user.Teacher;
import com.world.back.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

  @Autowired
  private TeacherService teacherService;

  // 获取教师列表（分页+搜索）
  @GetMapping("/list")
  public Result<Map<String, Object>> getTeacherList(
          @RequestParam(value = "institute_id", required = false) Integer instituteId,
          @RequestParam(value = "page", defaultValue = "1") Integer page,
          @RequestParam(value = "size", defaultValue = "10") Integer size,
          @RequestParam(value = "search", required = false) String search) {

    Map<String, Object> teacherList = teacherService.getTeacherList(instituteId, page, size, search);
    return Result.success(teacherList);
  }

  // 获取教师详情
  @GetMapping("/{id}")
  public Result<Teacher> getTeacher(@PathVariable String id) {
    Teacher teacher = teacherService.getTeacherById(id);
    return Result.success(teacher);
  }

  // 创建教师
  @PostMapping("/create")
  public Result<Boolean> createTeacher(@RequestBody Teacher teacher) {
    boolean result = teacherService.createTeacher(teacher);
    return result ? Result.success(true) : Result.error("创建失败");
  }

  // 更新教师
  @PostMapping("/update")
  public Result<Boolean> updateTeacher(@RequestBody Teacher teacher) {
    boolean result = teacherService.updateTeacher(teacher);
    return result ? Result.success(true) : Result.error("更新失败");
  }

  // 删除教师
  @DeleteMapping("/delete/{id}")
  public Result<Boolean> deleteTeacher(@PathVariable String id) {
    boolean result = teacherService.deleteTeacher(id);
    return result ? Result.success(true) : Result.error("删除失败");
  }

  // 设置答辩组长
  @PostMapping("/set-leader")
  public Result<Boolean> setDefenseLeader(
          @RequestParam("group_id") Integer groupId,
          @RequestParam("teacher_id") String teacherId) {

    boolean result = teacherService.setDefenseLeader(groupId, teacherId);
    return result ? Result.success(true) : Result.error("设置失败");
  }

  // 获取教师总数
  @GetMapping("/count")
  public Result<Long> getTeacherCount(
          @RequestParam(value = "institute_id", required = false) Integer instituteId) {

    Long count = teacherService.getTeacherCount(instituteId);
    return Result.success(count);
  }
  // 分配教师到小组（非组长）
  @PostMapping("/assign-group")
  public Result<Boolean> assignTeacherToGroup(
          @RequestParam("teacher_id") String teacherId,
          @RequestParam("group_id") Integer groupId) {

    // 这里需要实现将教师分配到小组的逻辑（非组长）
    // 具体实现需要根据你的业务需求来定
    // 可能需要修改 dbgroup 表结构，添加 teacher_id 字段来关联非组长教师

    return Result.success(true);
  }

  // 从小组中移除教师
  @PostMapping("/remove-group")
  public Result<Boolean> removeTeacherFromGroup(
          @RequestParam("teacher_id") String teacherId) {

    // 实现从小组中移除教师的逻辑
    // 如果该教师是组长，则需要清空 dbgroup 表的 admin_id
    // 如果只是普通成员，则需要从相关表中移除关联

    return Result.success(true);
  }
}