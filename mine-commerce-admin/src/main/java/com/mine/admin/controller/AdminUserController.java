package com.mine.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mine.admin.config.LogOperation;
import com.mine.common.result.Result;
import com.mine.model.entity.User;
import com.mine.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "后台管理用户接口")
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @ApiOperation("分页查询用户列表")
    @GetMapping("/list")
    public Result<IPage<User>> listUsers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(userService.listUsers(pageNum, pageSize));
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    @ApiOperation("更新用户状态")
    @PutMapping("/{id}/status")
    @LogOperation(module = "用户管理", action = "更新用户状态")
    public Result<Boolean> updateStatus(@PathVariable Long id,
                                        @RequestParam Integer status) {
        return Result.success(userService.updateStatus(id, status));
    }

    @ApiOperation("更新用户信息")
    @PutMapping
    @LogOperation(module = "用户管理", action = "更新用户信息")
    public Result<Boolean> updateUser(@RequestBody User user) {
        return Result.success(userService.updateUser(user));
    }
}
