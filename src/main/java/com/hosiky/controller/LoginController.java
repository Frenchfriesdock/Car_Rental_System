            package com.hosiky.controller;

import com.hosiky.common.Result;
import com.hosiky.domain.po.User;
import com.hosiky.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "统一登录")
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final IUserService userService;


    @Operation(summary = "用户名登录")
    @PostMapping("/username")
    public Result loginByUsername(@RequestBody User user) {
        return userService.loginByUsername(user);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/loginOut")
    public Result loginOut(@Valid @RequestHeader("Authorization") String access_token,
                           HttpServletResponse response) {
        return userService.loginOut(access_token, response);
    }
}
