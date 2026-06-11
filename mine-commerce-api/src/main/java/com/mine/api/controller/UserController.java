package com.mine.api.controller;

import com.mine.common.result.Result;
import com.mine.model.entity.User;
import com.mine.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "小程序用户接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<User> register(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String phone) {
        return Result.success(userService.register(username, password, phone));
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<User> login(@RequestParam String username,
                              @RequestParam String password) {
        return Result.success(userService.login(username, password));
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    @ApiOperation("更新用户信息")
    @PutMapping
    public Result<Boolean> updateUser(@RequestBody User user) {
        return Result.success(userService.updateUser(user));
    }
}
