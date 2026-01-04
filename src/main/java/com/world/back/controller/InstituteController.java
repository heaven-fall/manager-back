package com.world.back.controller;

import com.world.back.entity.Institute;
import com.world.back.entity.res.Result;
import com.world.back.serviceImpl.InstituteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/institute")
public class InstituteController {

    @Autowired
    private InstituteServiceImpl instituteService;

    @GetMapping("/list")
    public Result<List<Institute>> getInstituteList() {
        List<Institute> list = instituteService.getAll();
        return Result.success(list);
    }

    @GetMapping("/available")
    public Result<List<Institute>> getAvailableInstitutes() {
        List<Institute> list = instituteService.getAvailableInstitutes();
        return Result.success(list);
    }

    @GetMapping("/count")
    public Result<Long> getInstituteCount()
    {
        return Result.success(instituteService.getInstituteCount());
    }

    @PostMapping("/add")
    public Result<Boolean> addInstitute(@RequestBody Map<String, Object> map)
    {
        try {
            String name = map.get("name").toString();
            String adminId = null;

            // 处理adminId可能为空的情况
            if (map.containsKey("adminId")) {
                Object adminIdObj = map.get("adminId");
                if (adminIdObj != null && !adminIdObj.toString().trim().isEmpty()) {
                    adminId = adminIdObj.toString();
                }
            }

            Institute institute = new Institute(name, adminId);
            instituteService.addInstitute(institute);
            return Result.success(true);
        } catch (Exception e) {
            return Result.error("创建失败: " + e.getMessage());
        }
    }

    @PostMapping("/update")
    public Result<Boolean> updateInstitute(@RequestBody Map<String, Object> map) {
        try {
            Integer id = Integer.parseInt(map.get("id").toString());
            String name = map.get("name").toString();
            String adminId = map.get("adminId") != null ? map.get("adminId").toString() : null;

            Institute institute = new Institute();
            institute.setId(id);
            institute.setName(name);
            institute.setAdminId(adminId);

            return Result.success(instituteService.updateInstitute(institute));
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    @GetMapping("/admin/{adminId}/institutes")
    public Result<List<Institute>> getInstitutesByAdminId(@PathVariable String adminId) {
        try {
            List<Institute> institutes = instituteService.getInstitutesByAdminId(adminId);
            return Result.success(institutes);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}