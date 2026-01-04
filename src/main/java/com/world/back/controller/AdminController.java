package com.world.back.controller;

import com.world.back.entity.Institute;
import com.world.back.entity.res.Result;
import com.world.back.entity.user.Admin;
import com.world.back.entity.user.InstituteAdmin;
import com.world.back.serviceImpl.InstituteServiceImpl;
import com.world.back.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private InstituteServiceImpl instituteService;

    @PostMapping("/create")
    public Result<String> createInstituteAdmin(@RequestBody Map<String, Object> data) {
        try {
            String realName = data.get("realName").toString();
            String instituteIdStr = data.get("institute").toString();
            String username = data.get("username").toString();
            String password = data.get("password").toString();
            String phone = data.get("phone") != null ? data.get("phone").toString() : "";
            String email = data.get("email") != null ? data.get("email").toString() : "";

            // 验证院系是否存在且未安排管理员
            Integer instituteId = Integer.parseInt(instituteIdStr);
            Institute institute = instituteService.getAll().stream()
                    .filter(inst -> inst.getId().equals(instituteId))
                    .findFirst()
                    .orElse(null);

            if (institute == null) {
                return Result.error("院系不存在");
            }

            if (institute.getAdminId() != null && !institute.getAdminId().isEmpty()) {
                return Result.error("该院系已有管理员");
            }

            // 创建管理员用户
            InstituteAdmin instituteAdmin = new InstituteAdmin();
            instituteAdmin.setRealName(realName);
            instituteAdmin.setPwd(password);
            instituteAdmin.setRole(1); // 院系管理员角色
            instituteAdmin.setPhone(phone);
            instituteAdmin.setEmail(email);
            instituteAdmin.setId(username);
            instituteAdmin.setInstId(instituteId);  // 设置院系ID

            // 创建用户
            boolean userCreated = userService.createAdmin(instituteAdmin);
            if (!userCreated) {
                return Result.error("创建用户失败");
            }

            // 创建用户与院系关联
            boolean relationCreated = userService.createUserInstRel(username, instituteId);
            if (!relationCreated) {
                // 回滚：删除已创建的用户
                userService.deleteInstituteAdmin(username);
                return Result.error("创建关联失败");
            }

            // 更新院系的管理员字段
            institute.setAdminId(username);
            instituteService.updateInstitute(institute);

            return Result.success("创建成功");
        } catch (Exception e) {
            return Result.error("创建失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<Admin>> getInstituteAdmins() {
        try {
            List<Admin> admins = userService.getAllAdmins();
            for (Admin admin : admins) {
                // 获取管理员管理的所有院系
                List<Institute> institutes = instituteService.getInstitutesByAdminId(admin.getId());
                if (institutes != null && !institutes.isEmpty()) {
                    StringBuilder instituteNames = new StringBuilder();
                    for (Institute institute : institutes) {
                        if (instituteNames.length() > 0) {
                            instituteNames.append(", ");
                        }
                        instituteNames.append(institute.getName());
                    }
                    admin.setPwd(instituteNames.toString());
                } else {
                    admin.setPwd("未管理院系");
                }
            }
            return Result.success(admins);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    // 新增：获取未安排管理员的院系
    @GetMapping("/available-institutes")
    public Result<List<Institute>> getAvailableInstitutes() {
        try {
            List<Institute> institutes = instituteService.getAvailableInstitutes();
            return Result.success(institutes);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @PostMapping("/update")
    public Result<Boolean> updateInstituteAdmin(@RequestBody Map<String, Object> data)
    {
        try {
            String realName = data.get("realName").toString();
            String username = data.get("id").toString();
            String phone = data.get("phone") != null ? data.get("phone").toString() : "";
            String email = data.get("email") != null ? data.get("email").toString() : "";

            // 更新管理员基本信息
            Boolean result = userService.updateAdmin(realName, username, phone, email);

            return Result.success(result);
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    @GetMapping("/count")
    public Result<Integer> getCount()
    {
        return Result.success(userService.getAdminCount());
    }

    @PostMapping("/delete")
    public Result<Boolean> deleteInstituteAdmin(@RequestBody Map<String, Object> data){
        try {
            String id = data.get("id").toString();

            // 删除前，先将该管理员管理的院系的管理员ID设为null
            List<Institute> institutes = instituteService.getInstitutesByAdminId(id);
            for (Institute institute : institutes) {
                institute.setAdminId(null);
                instituteService.updateInstitute(institute);
            }

            // 删除用户与院系的关联
            for (Institute institute : institutes) {
                userService.deleteUserInstRel(id, institute.getId());
            }

            // 删除用户
            // 由于UserMapper的deleteUser方法需要int参数，我们需要调整
            // 这里我们使用deleteUserById方法
            return Result.success(userService.deleteInstituteAdmin(id));
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    @PostMapping("/resetPassword")
    public Result<Boolean> resetPassword(@RequestBody Map<String, Object> data)
    {
        try {
            String id = data.get("id").toString();
            return Result.success(userService.resetPassword(id));
        } catch (Exception e) {
            return Result.error("重置密码失败: " + e.getMessage());
        }
    }
}