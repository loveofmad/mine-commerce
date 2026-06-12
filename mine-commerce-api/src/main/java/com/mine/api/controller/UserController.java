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

    @ApiOperation("微信登录")
    @PostMapping("/wx-login")
    public Result<User> wxLogin(@RequestBody java.util.Map<String, String> params) {
        String code = params.get("code");
        return Result.success(userService.wxLogin(code));
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
