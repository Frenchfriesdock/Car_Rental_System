package com.hosiky.service;

import com.hosiky.common.Result;
import com.hosiky.domain.dto.UserRegisterDto;
import com.hosiky.domain.po.Car;
import com.hosiky.domain.po.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

@Service
public interface IUserService  {

    /**
     * 用户登录
     * @param user
     * @return
     */
    Result loginByUsername(User user);

    User register(UserRegisterDto userRegisterDto);

    void sendCode(@NotBlank String email);
}
