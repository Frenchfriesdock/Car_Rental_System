package com.hosiky.controller;

import com.hosiky.common.Result;
import com.hosiky.domain.po.User;
import com.hosiky.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "统一登录")
@RestController
@RequestMapping("/login/")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final IUserService userService;


    @Operation(summary = "用户名登录")
    @PostMapping("username")
    public Result loginByUsername(@RequestBody User user) {
        return userService.loginByUsername(user);
    }
}
