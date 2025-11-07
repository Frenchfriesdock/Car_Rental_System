package com.hosiky.controller;


import com.hosiky.common.Result;
import com.hosiky.domain.po.User;
import com.hosiky.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户接口")
@RestController
@RequestMapping("/user/")
@RequiredArgsConstructor
@Slf4j

/**
 * 与用户相关的接口
 */
public  class UserController {

    private final IUserService userService;


    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result Register(User user) {
        return null;
    }

    @Operation(summary = "修改用户")
    @PutMapping("/userUpdate")
    public Result UpdateUser(User user) {
        return null;
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/id")
    public Result deleteUser(Integer id) {
        return null;
    }

    @Operation(summary = "查看用户")
    @GetMapping("/id")
    public Result showUser(Integer id) {
        return null;
    }
}
