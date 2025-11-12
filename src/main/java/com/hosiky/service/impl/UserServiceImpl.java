package com.hosiky.service.impl;


import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hosiky.client.MailClient;
import com.hosiky.client.RedisClient;
import com.hosiky.common.Result;
import com.hosiky.constant.RedisConstant;
import com.hosiky.domain.dto.UserRegisterDto;
import com.hosiky.domain.po.Car;
import com.hosiky.domain.po.User;
import com.hosiky.mapper.UserMapper;
import com.hosiky.properties.JwtProperties;
import com.hosiky.security.entity.MyUserDetail;
import com.hosiky.service.IUserService;
import com.hosiky.utils.JwtUtils;
import com.hosiky.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final RedisUtil redisUtil;
    private final JwtUtils jwtUtils;
    private final RedisClient redisClient;
    private final MailClient mailClient;
    private final AuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties;


    @Override
    public Result loginByUsername(User user) {
//        封装用户名和密码
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword());

        // 调用认证管理中的认证方法，调用后可抛出异常。所以需要try...catch
        // 封装用户信息至SpringSecurity上下文中
        try {
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authenticate);
            if(Objects.isNull(authenticate)) {
                throw new AuthenticationServiceException("认证失败，用户或密码错误");
            }

            MyUserDetail myUserDetail = (MyUserDetail) authenticate.getPrincipal();
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
            String key = "User:" + id + ":" + token;
            long redisExpireSeconds = jwtProperties.getTtl() / 1000;  // 7200s = 2h
            redisUtil.setex(key, JSON.toJSONString(myUserDetail), redisExpireSeconds);

            return Result.ok(token);
        } catch (AuthenticationException e) {
            throw new AuthenticationServiceException("认证失败,用户名或密码错误");
        }
    }

    @Override
    public User register(UserRegisterDto userRegisterDto) {
        String code = userRegisterDto.getCode();
        String email = userRegisterDto.getEmail();

        String verificationCode = (String) redisClient.get(RedisConstant.User_CODE + email);
        if(Objects.isNull(verificationCode) || !verificationCode.equals(code)) {
            throw new RuntimeException(RedisConstant.CODE_ERROR);
        }

        User user = lambdaQuery()
                .eq(User::getEmail, email)
                .one();

        if(Objects.nonNull(user)) {
            throw new RuntimeException(RedisConstant.USER_EXIT);
        }

        User newUser = new User();

        newUser.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        newUser.setStatus(1);
        newUser.setEmail(email);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        this.save(newUser);

        //        自动清除验证码
        redisClient.delete(RedisConstant.User_CODE + email);
        return newUser;
    }

    @Override
    public void sendCode(String email) {
//        锁标识的键
        String lock_key = RedisConstant.User_CODE_LOCK + email;
//        code的键
        String code_key = RedisConstant.User_CODE_LOCK + email;
       if(redisClient.hasKey(lock_key)) {
           throw new RuntimeException(RedisConstant.CODE_EXIT);
       }

        String code = generateCode();
//       将code存入到redis里面
        redisClient.set(code_key, code,5, TimeUnit.MINUTES);
//        设置锁标识，防止短时间内重复发送验证码
        redisClient.set(lock_key,"1",1,TimeUnit.MINUTES);
//        发送邮件
        mailClient.sendMail(email,code);
    }

    private String generateCode() {
        return RandomUtil.randomNumbers(6);
    }
}
