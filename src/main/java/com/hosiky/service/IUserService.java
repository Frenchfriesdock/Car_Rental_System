package com.hosiky.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hosiky.common.*;
import com.hosiky.domain.dto.userDto.UserRegisterDto;
import com.hosiky.domain.dto.userDto.UserUpdateDto;
import com.hosiky.domain.po.User;
import com.hosiky.domain.vo.UserVo;
import jakarta.validation.constraints.NotBlank;


public interface IUserService extends IService<User> {

    /**
     * 用户登录
     * @param user
     * @return
     */
    Result loginByUsername(User user);

    User register(UserRegisterDto userRegisterDto);

    String sendCode(@NotBlank String email);

    Result updateUser(UserUpdateDto userUpdateDto);

    UserVo getUserVo(Integer id);

    ResultUtil<Page<User>> listUser(PageParameter<User> userQuery);

//    Result deleteUserTruly();

}
