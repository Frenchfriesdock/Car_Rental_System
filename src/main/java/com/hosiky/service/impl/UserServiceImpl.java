package com.hosiky.service.impl;


import com.alibaba.fastjson2.JSON;
import com.hosiky.common.Result;
import com.hosiky.domain.po.User;
import com.hosiky.security.entity.MyUserDetail;
import com.hosiky.service.IUserService;
import com.hosiky.utils.JwtUtils;
import com.hosiky.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final RedisUtil redisUtil;
    private final JwtUtils jwtUtils;

    @Override
    public Result loginByUsername(User user) {
//        封装用户名和密码
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword());

        // 调用认证管理中的认证方法，调用后可抛出异常。所以需要try...catch
        // 封装用户信息至SpringSecurity上下文中
        try {
            MyUserDetail myUserDetail = (MyUserDetail) authenticationToken.getPrincipal();
            if(Objects.isNull(myUserDetail)) {
                throw new AuthenticationServiceException("认证失败，用户名或者密码错误");
            }

//            清理redis
            String id = myUserDetail.getUser().getId().toString();
            Set<String> keys = redisUtil.scan("User:" + id + ":*");
            if(!keys.isEmpty()) {
                redisUtil.delete(keys);
            }

//            生成token
            Map<String, Object> claims = Map.of(
                    "id", id
            );
            String token = jwtUtils.generateJwt(claims);

//            写入redis
            String key = "User:" + id + token;
            redisUtil.set(key, JSON.toJSONString(myUserDetail));

            return Result.ok(token);
        } catch (AuthenticationException e) {
            throw new AuthenticationServiceException("认证失败,用户名或密码错误");
        }
    }
}
