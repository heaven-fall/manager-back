package com.world.back.controller;

import com.world.back.entity.Student;
import com.world.back.entity.res.Result;
import com.world.back.service.InstituteService;
import com.world.back.service.StudentService;
import com.world.back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private InstituteService instituteService;
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getStudentList(
            @RequestParam Integer institute_id,
            @RequestParam Integer currentpage,
            @RequestParam Integer pagesize) {

        try {
            List<Map<String, Object>> result = studentService.getStudentListPage(institute_id, currentpage, pagesize);
            for (Map<String, Object> map : result) {
                map.put("instituteName", instituteService.getInstituteNameById((Integer) map.get("institute_id")));
                map.put("teacherName", userService.getNameById(studentService.getTeacherById((String) map.get("id"))));;
            }
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/listunassign")
    public Result<List<Map<String, Object>>> getStudentListUnassign(
            @RequestParam Integer institute_id,
            @RequestParam Integer currentpage,
            @RequestParam Integer pagesize)
    {
        try {
            List<Map<String, Object>> result = studentService.getStudentListPage(institute_id, currentpage, pagesize);
            List<Map<String, Object>> res = new ArrayList<>();
            for (int i = 0; i < result.size(); i++) {
                if (studentService.getGidBySid((String)result.get(i).get("id")) == null)
                {
                    Map<String, Object> item = result.get(i);
                    item.put("instituteName", instituteService.getInstituteNameById((Integer) result.get(i).get("institute_id")));
                    item.put("teacherName", userService.getNameById(studentService.getTeacherById((String) result.get(i).get("id"))));;
                    res.add(item);
                }
            }
            return Result.success(res);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/count")
    public Result<Integer> count(@RequestParam Integer institute_id){
        return Result.success(studentService.getCount(institute_id));
    }
    
    @GetMapping("/unassigncount")
    public Result<Integer> unassigncount(@RequestParam Integer institute_id)
    {
        return Result.success(studentService.getUnassignCount(institute_id));
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
    public ResponseEntity<Map<String, Object>> assignGroup(@RequestBody Map<String, Object> map) {
        String student_id = (String) map.get("student_id");
        Integer group_id = (Integer) map.get("group_id");
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
