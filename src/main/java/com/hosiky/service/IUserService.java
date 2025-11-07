package com.hosiky.service;

import com.hosiky.common.Result;
import com.hosiky.domain.po.User;
import org.springframework.stereotype.Service;

@Service
public interface IUserService  {

    /**
     * 用户登录
     * @param user
     * @return
     */
    Result loginByUsername(User user);
}
