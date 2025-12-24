package com.world.back.controller;

import com.world.back.entity.Student;
import com.world.back.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * 获取学生列表
     * GET /api/students/list
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getStudentList(
            @RequestParam Long institute_id,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        try {
            Map<String, Object> result = studentService.getStudentList(institute_id, search, page, size);
            return ResponseEntity.ok(buildSuccessResponse(result));
        } catch (Exception e) {
            return ResponseEntity.ok(buildErrorResponse(e.getMessage()));
        }
    }

    /**
     * 创建学生
     * POST /api/students/create
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createStudent(@RequestBody Student student) {
        try {
            // 学号即id
            if (student.getId() == null || student.getId().isEmpty()) {
                return ResponseEntity.ok(buildErrorResponse("学号不能为空"));
            }

            boolean success = studentService.createStudent(student);
            if (success) {
                return ResponseEntity.ok(buildSuccessResponse("创建成功"));
            } else {
                return ResponseEntity.ok(buildErrorResponse("创建失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(buildErrorResponse(e.getMessage()));
        }
    }

    /**
     * 更新学生
     * POST /api/students/update
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateStudent(@RequestBody Student student) {
        try {
            if (student.getId() == null || student.getId().isEmpty()) {
                return ResponseEntity.ok(buildErrorResponse("学生ID不能为空"));
            }

            boolean success = studentService.updateStudent(student);
            if (success) {
                return ResponseEntity.ok(buildSuccessResponse("更新成功"));
            } else {
                return ResponseEntity.ok(buildErrorResponse("更新失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(buildErrorResponse(e.getMessage()));
        }
    }

    /**
     * 删除学生
     * DELETE /api/students/delete/{id}
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteStudent(@PathVariable String id) {
        try {
            boolean success = studentService.deleteStudent(id);
            if (success) {
                return ResponseEntity.ok(buildSuccessResponse("删除成功"));
            } else {
                return ResponseEntity.ok(buildErrorResponse("删除失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(buildErrorResponse(e.getMessage()));
        }
    }

    /**
     * 分配答辩小组
     * POST /api/students/assign-group
     */
    @PostMapping("/assign-group")
    public ResponseEntity<Map<String, Object>> assignGroup(
            @RequestParam String student_id,
            @RequestParam Long group_id) {
        try {
            boolean success = studentService.assignGroup(student_id, group_id);
            if (success) {
                return ResponseEntity.ok(buildSuccessResponse("分配成功"));
            } else {
                return ResponseEntity.ok(buildErrorResponse("分配失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(buildErrorResponse(e.getMessage()));
        }
    }

    /**
     * 检查学号是否重复
     * GET /api/students/check-student-id
     */
    @GetMapping("/check-student-id")
    public ResponseEntity<Map<String, Object>> checkStudentId(
            @RequestParam String student_id,
            @RequestParam(required = false) String exclude_id) {
        try {
            boolean isDuplicate = studentService.isStudentIdDuplicate(student_id, exclude_id);
            Map<String, Object> data = new HashMap<>();
            data.put("isDuplicate", isDuplicate);
            data.put("message", isDuplicate ? "学号已存在" : "学号可用");
            return ResponseEntity.ok(buildSuccessResponse(data));
        } catch (Exception e) {
            return ResponseEntity.ok(buildErrorResponse("检查失败"));
        }
    }

    /**
     * 构建成功响应
     */
    private Map<String, Object> buildSuccessResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", data);
        return response;
    }

    /**
     * 构建错误响应
     */
    private Map<String, Object> buildErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", message);
        response.put("data", null);
        return response;
    }
}