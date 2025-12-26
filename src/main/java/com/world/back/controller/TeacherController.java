package com.world.back.controller;

import com.world.back.entity.res.Result;
import com.world.back.entity.user.Teacher;
import com.world.back.mapper.TeacherMapper;
import com.world.back.service.TeacherService;
import com.world.back.serviceImpl.TeacherServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

  @Autowired
  private TeacherService teacherService;

  @Autowired
  private TeacherServiceImpl teacherServiceImpl;

  // 获取教师列表（分页+搜索）
  @GetMapping("/list")
  public ResponseEntity<?> getTeacherList(
          @RequestParam(required = false) Integer institute_id) {
    try {
      List<Teacher> result = teacherService.getTeacherList(institute_id);
      return ResponseEntity.ok(Map.of(
              "success", true,
              "code", 200,
              "message", "成功",
              "data", result
      ));
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of(
              "success", false,
              "code", 500,
              "message", e.getMessage()
      ));
    }
  }

  // 获取教师详情
  @GetMapping("/{id}")
  public ResponseEntity<?> getTeacherById(@PathVariable String id) {
    try {
      Teacher teacher = teacherService.getTeacherById(id);
      return ResponseEntity.ok(Map.of(
              "success", true,
              "code", 200,
              "message", "成功",
              "data", teacher
      ));
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of(
              "success", false,
              "code", 500,
              "message", e.getMessage()
      ));
    }
  }

  // 创建教师
  @PostMapping("/create")
  public ResponseEntity<?> createTeacher(@RequestBody Teacher teacher) {
    try {
      boolean result = teacherService.createTeacher(teacher);
      return ResponseEntity.ok(Map.of(
              "success", result,
              "code", result ? 200 : 500,
              "message", result ? "创建成功" : "创建失败"
      ));
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of(
              "success", false,
              "code", 500,
              "message", e.getMessage()
      ));
    }
  }

  // 删除教师
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deleteTeacher(@PathVariable String id) {
    try {
      boolean result = teacherService.deleteTeacher(id);
      return ResponseEntity.ok(Map.of(
              "success", result,
              "code", result ? 200 : 500,
              "message", result ? "删除成功" : "删除失败"
      ));
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of(
              "success", false,
              "code", 500,
              "message", e.getMessage()
      ));
    }
  }

  // 获取教师总数
  @GetMapping("/count")
  public Result<Long> getTeacherCount(
          @RequestParam(value = "institute_id", required = false) Integer instituteId) {

    Long count = teacherService.getTeacherCount(instituteId);
    return Result.success(count);
  }

  // 添加教师到小组
  @PostMapping("/add-to-group")
  public ResponseEntity<?> addToGroup(
          @RequestParam String teacher_id,
          @RequestParam Integer group_id,
          @RequestParam(required = false, defaultValue = "false") Boolean is_leader) {
    try {
      boolean result = teacherService.addTeacherToGroup(teacher_id, group_id, is_leader);

      if (result) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "code", 200,
                "message", is_leader ? "成功加入小组并设为组长" : "成功加入小组"
        ));
      } else {
        // 如果返回false，可能是小组已有组长
        return ResponseEntity.ok(Map.of(
                "success", false, // 注意这里也返回true
                "code", 200,
                "message", "失败"
        ));
      }
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of(
              "success", false,
              "code", 500,
              "message", e.getMessage()
      ));
    }
  }

  // 从小组中移除教师
  @PostMapping("/remove-from-group")
  public ResponseEntity<?> removeFromGroup(
          @RequestParam String teacher_id,
          @RequestParam Integer group_id) {
    try {
      boolean result = teacherService.removeTeacherFromGroup(teacher_id, group_id);
      return ResponseEntity.ok(Map.of(
              "success", result,
              "code", result ? 200 : 500,
              "message", result ? "移除成功" : "移除失败"
      ));
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of(
              "success", false,
              "code", 500,
              "message", e.getMessage()
      ));
    }
  }


  // 获取可用年份
  @GetMapping("/years")
  public ResponseEntity<?> getYears() {
    try {
      List<Integer> years = teacherService.getAvailableYears();
      return ResponseEntity.ok(Map.of(
              "success", true,
              "code", 200,
              "message", "成功",
              "data", years
      ));
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of(
              "success", false,
              "code", 500,
              "message", e.getMessage()
      ));
    }
  }

  // 获取某年份下的小组
  @GetMapping("/groups-by-year")
  public ResponseEntity<?> getGroupsByYear(@RequestParam Integer year) {
    try {
      List<Integer> groups = teacherService.getGroupsByYear(year);
      return ResponseEntity.ok(Map.of(
              "success", true,
              "code", 200,
              "message", "成功",
              "data", groups
      ));
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of(
              "success", false,
              "code", 500,
              "message", e.getMessage()
      ));
    }
  }

  // 检查教师是否在某年份已有小组
  @GetMapping("/check-year")
  public ResponseEntity<?> checkTeacherInYear(
          @RequestParam String teacher_id,
          @RequestParam Integer year) {
    try {
      boolean result = teacherService.isTeacherInYear(teacher_id, year);
      return ResponseEntity.ok(Map.of(
              "success", true,
              "code", 200,
              "message", "成功",
              "data", result
      ));
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of(
              "success", false,
              "code", 500,
              "message", e.getMessage()
      ));
    }
  }

  // 设置答辩组长
  @PostMapping("/set-defense-leader")
  public ResponseEntity<?> setDefenseLeader(
          @RequestParam Integer group_id,
          @RequestParam String teacher_id) {
    try {
      boolean result = teacherService.setDefenseLeader(group_id, teacher_id);
      return ResponseEntity.ok(Map.of(
              "success", result,
              "code", result ? 200 : 500,
              "message", result ? "设置成功" : "设置失败"
      ));
    } catch (Exception e) {
      // 如果是组长已存在的特殊提示，返回特定code
      if (e.getMessage().contains("已有答辩组长")) {
        return ResponseEntity.ok(Map.of(
                "success", false,
                "code", 400, // 业务错误码
                "message", e.getMessage(),
                "data", Map.of("hasLeader", true)
        ));
      }
      return ResponseEntity.ok(Map.of(
              "success", false,
              "code", 500,
              "message", e.getMessage()
      ));
    }
  }

  // 清除答辩组长
  @PostMapping("/clear-defense-leader")
  public Result<Boolean> clearDefenseLeader(@RequestParam("group_id") Integer groupId) {
    boolean result = teacherServiceImpl.clearDefenseLeader(groupId);
    return result ? Result.success(true) : Result.error("清除失败");
  }
  // 调试接口：获取教师的小组信息
  @GetMapping("/debug/{id}/groups")
  public Result<List<Teacher.GroupInfo>> getTeacherGroups(@PathVariable String id) {
    Teacher teacher = teacherService.getTeacherById(id);
    if (teacher == null) {
      return Result.error("教师不存在");
    }
    return Result.success(teacher.getGroups());
  }


  // 调试接口2：获取完整教师信息
  @GetMapping("/debug/{id}/full")
  public Result<Teacher> getTeacherFull(@PathVariable String id) {
    Teacher teacher = teacherService.getTeacherById(id);
    if (teacher == null) {
      return Result.error("教师不存在");
    }


    if (teacher.getGroups() != null) {
      for (Teacher.GroupInfo group : teacher.getGroups()) {
      }
    }

    return Result.success(teacher);
  }
}
