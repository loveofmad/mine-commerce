package com.mine.api.controller;

import com.mine.common.result.Result;
import com.mine.common.util.JwtUtil;
import com.mine.model.entity.User;
import com.mine.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "小程序用户接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation("微信登录")
    @PostMapping("/wx-login")
    public Result<Map<String, Object>> wxLogin(@RequestBody java.util.Map<String, String> params) {
        String code = params.get("code");
        User user = userService.wxLogin(code);
        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), "USER");
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        return Result.success(data);
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
