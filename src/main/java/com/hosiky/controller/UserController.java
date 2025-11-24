package com.hosiky.controller;


import com.hosiky.common.Result;
import com.hosiky.domain.dto.userDto.UserRegisterDto;
import com.hosiky.domain.dto.userDto.UserUpdateDto;
import com.hosiky.domain.vo.UserVo;
import com.hosiky.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.web.bind.annotation.*;


@Tag(name = "用户接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j

/**
 * 与用户相关的接口
 */
public  class UserController {

    private final IUserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result Register(UserRegisterDto userRegisterDto) {
        return Result.ok(userService.register(userRegisterDto));
    }

    /**
     * @param userUpdateDto
     * @return
     */
    @Operation(summary = "修改用户")
    @PostMapping("/userUpdate")
    public Result UpdateUser(@RequestBody UserUpdateDto userUpdateDto) {
        UserVo userVo = userService.updateUser(userUpdateDto);
       return Result.ok(userVo);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/id")
    public Result deleteUser(Integer id) {
        userService.removeById(id);
        return Result.ok("删除用户成功");
    }

    /**
     * 这个权限给管理员，管理员可以查看到用户的密码啥的
     * @param id
     * @return
     */
    @Operation(summary = "查看用户")
    @GetMapping("/id")
    public Result showUser(Integer id) {
        UserVo userVo = userService.getUserVo(id);
        return Result.ok(userVo);
    }

    @Operation(summary = "短信验证")
    @PostMapping("/sendEmail")
    public Result sendCode(@RequestParam @NotBlank String email) {
        userService.sendCode(email);
        return Result.ok();
    }


}
