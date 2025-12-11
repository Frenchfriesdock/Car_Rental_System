package com.hosiky.service.impl;


import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hosiky.client.MailClient;
import com.hosiky.client.RedisClient;
import com.hosiky.common.*;
import com.hosiky.constant.RedisConstant;
import com.hosiky.domain.dto.userDto.UserRegisterDto;
import com.hosiky.domain.dto.userDto.UserUpdateDto;
import com.hosiky.domain.dto.userDto.UserUpdatePasswordDto;
import com.hosiky.domain.po.User;
import com.hosiky.domain.vo.UserVo;
import com.hosiky.mapper.UserMapper;
import com.hosiky.properties.JwtProperties;
import com.hosiky.security.entity.MyUserDetail;
import com.hosiky.service.IUserService;
import com.hosiky.utils.JwtUtils;
import com.hosiky.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
import java.util.UUID;
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
    private final UserMapper userMapper;

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
        String username = userRegisterDto.getUsername();

        String verificationCode = (String) redisClient.get(RedisConstant.User_CODE_KEY + email);
        if(Objects.isNull(verificationCode) || !verificationCode.equals(code)) {
            throw new RuntimeException(RedisConstant.CODE_ERROR);
        }

        User user = lambdaQuery()
                .eq(User::getUsername, username)
                .one();

        if(Objects.nonNull(user)) {
            throw new RuntimeException(RedisConstant.USER_EXIT);
        }

        User newUser = new User();

        newUser.setId((UUID.randomUUID().toString().replace("-", "")));
        newUser.setPassword(BCrypt.hashpw(userRegisterDto.getPassword(), BCrypt.gensalt()));
        newUser.setUsername(username);
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
    public String sendCode(String email) {
//        锁标识的键
        String lock_key = RedisConstant.User_CODE_LOCK + email;
//        code的键
        String email_code = RedisConstant.User_CODE_KEY + email;
       if(redisClient.hasKey(lock_key)) {
           throw new RuntimeException(RedisConstant.CODE_EXIT);
       }

        String code = generateCode();
       try {
           //       将code存入到redis里面
           redisClient.set(email_code, code,5, TimeUnit.MINUTES);
//        设置锁标识，防止短时间内重复发送验证码
           redisClient.set(lock_key,"1",3,TimeUnit.MINUTES);
//        发送邮件
           mailClient.sendMail(email,code);
           return code;
       } catch (Exception e) {
           throw new RuntimeException(e);
       }

    }

    @Override
    public Result updateUser(UserUpdateDto userUpdateDto) {

        User user = this.getById(userUpdateDto.getId());
        if(Objects.isNull(user)) {
            return Result.errorMsg("该用户信息不存在");
        }
//        String password = BCrypt.hashpw(userUpdateDto.getPassword(), BCrypt.gensalt());
        // 1. 构建更新条件
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, userUpdateDto.getId())
                .set(userUpdateDto.getPhone() != null,User::getPhone, userUpdateDto.getPhone())
                .set(User::getIdCard, userUpdateDto.getIdCard())
                .set(User::getBirthday, userUpdateDto.getBirthday());

        // 2. 执行更新操作
        boolean isSuccess = update(updateWrapper);

        if (!isSuccess) {
            throw new RuntimeException("用户更新失败");
        }

        // 3. 查询更新后的用户信息
        User updatedUser = getById(userUpdateDto.getId());

        // 4. 转换为VO对象
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(updatedUser, userVo);

        return Result.ok(userVo);
    }

    @Override
    public UserVo getUserVo(Integer id) {
        User user = this.getById(id);
//        User user = this.userMapper.getUserTest(id);
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        return userVo;
    }

    @Override
    public ResultUtil<Page<User>> listUser(PageParameter<User> userQuery) {
        try {

            Page<User> page = new Page<>(userQuery.getPage(), userQuery.getLimit());

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            if(userQuery.getData().getUsername() != null && !userQuery.getData().getUsername().equals("")) {
                queryWrapper.like(User::getUsername, userQuery.getData().getUsername());
            }
            if(userQuery.getData().getEmail() != null && !userQuery.getData().getEmail().equals("")) {
                queryWrapper.eq(User::getEmail, userQuery.getData().getEmail());
            }

            Page<User> userPage = this.userMapper.selectPage(page, queryWrapper);
//            ByPageResult<User> userList = baseMapper.getUserList(userQuery);
            return ResultUtil.build(200, userPage,"查询用户成功");
        } catch (Exception e) {

            e.printStackTrace();
            return ResultUtil.error("查询失败");
        }
    }

    @Override
    public Boolean updatePassword(UserUpdatePasswordDto userUpdatePasswordDto) {
        User user = this.getById(userUpdatePasswordDto.getId());
        if(!BCrypt.checkpw(userUpdatePasswordDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        if(!Objects.equals(userUpdatePasswordDto.getNewPassword(), userUpdatePasswordDto.getConfirmPassword())) {
            throw new RuntimeException("两次密码不正确");
        }
        String newHash = BCrypt.hashpw(userUpdatePasswordDto.getNewPassword(), BCrypt.gensalt());
        user.setPassword(newHash);
        return this.updateById(user);
    }

    @Override
    public Result loginOut(String accessToken, HttpServletResponse response) {
        // 1. 去掉 "Bearer " 前缀
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        // 2. 解析 jwt 拿用户 id
        String userId;
        try {
            Claims claims = jwtUtils.parseJWT(accessToken);
            userId = claims.getId();
//            userId = jwtUtils.parseJWT(accessToken).get("id", String.class);
        } catch (Exception e) {
            // token 非法也返回成功，反正目的是“让它失效”
            return Result.ok("已退出");
        }

        // 3. 删 Redis 里以 "User:{id}:{token}" 为模式的所有 key
        Set<String> keys = redisUtil.scan("User:" + userId + ":*");
        if (!keys.isEmpty()) {
            redisUtil.delete(keys);
        }

        // 4. 清 SpringSecurity 上下文（防止并发线程里还残留）
        SecurityContextHolder.clearContext();

        // 5. 让前端也删 cookie（可选）
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return Result.ok("退出成功");
    }

//    @Override
//    public Result deleteUserTruly() {
//        this.userMapper.deleteUserTruly();
//        return Result.ok("物理删除成功");
//    }


    private String generateCode() {
        return RandomUtil.randomNumbers(6);
    }
}
