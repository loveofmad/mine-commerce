package com.mine.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mine.common.result.Result;
import com.mine.model.entity.Admin;
import com.mine.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "后台管理员认证接口")
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto) {
        Admin admin = adminService.login(dto.getUsername(), dto.getPassword());
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("token", "admin-" + admin.getId());
        data.put("admin", admin);
        return Result.success(data);
    }

    @ApiOperation("获取管理员信息")
    @GetMapping("/{id}")
    public Result<Admin> getAdmin(@PathVariable Long id) {
        return Result.success(adminService.getAdminById(id));
    }

    @ApiOperation("分页查询管理员列表")
    @GetMapping("/list")
    public Result<IPage<Admin>> listAdmins(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(adminService.listAdmins(pageNum, pageSize));
    }

    @ApiOperation("添加管理员")
    @PostMapping
    public Result<Boolean> addAdmin(@RequestBody Admin admin) {
        return Result.success(adminService.addAdmin(admin));
    }

    @ApiOperation("更新管理员")
    @PutMapping
    public Result<Boolean> updateAdmin(@RequestBody Admin admin) {
        return Result.success(adminService.updateAdmin(admin));
    }

    @ApiOperation("删除管理员")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteAdmin(@PathVariable Long id) {
        return Result.success(adminService.deleteAdmin(id));
    }

    @ApiOperation("更新管理员状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id,
                                        @RequestParam Integer status) {
        return Result.success(adminService.updateStatus(id, status));
    }

    @Data
    static class LoginDTO {
        private String username;
        private String password;
    }
}
