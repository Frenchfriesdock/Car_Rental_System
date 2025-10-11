package com.hosiky.security.entity;


import com.alibaba.fastjson.annotation.JSONField;
import com.hosiky.domain.po.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 这是SpringSecurity关于用户的认证、权限的接口，用来存储用户的个人信息、角色、权限等
 * 因此我们需要把数据库里的用户数据“包一层”转成 UserDetails 才能被框架拿来比对密码、做授权、会话管理等
 *
 */
@Data
public class MyUserDetail implements UserDetails {
    // 用户信息
    private User user;
    // 角色
    private List<String> roles;
    // 权限信息
    private List<String> permissions;

    /**
     *SimpleGrantedAuthority 是 Spring Security 提供的最简权限对象，一行代码就能表示一条权限（或角色）。
     */
    @JSONField(serialize = false)
    private List<SimpleGrantedAuthority> authorities;  // 缓存结果，避免重复拼装

    public MyUserDetail(User user, List<String> roles, List<String> permissions) {
        this.user = user;
        this.roles = roles;
        this.permissions = permissions;
    }

    /**
     * 授权信息
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 将权限信息封装成 SimpleGrantedAuthority
        if (authorities != null) {
            return authorities;
        }
        authorities = new ArrayList<>();
        roles.forEach(item -> authorities.add(new SimpleGrantedAuthority("ROLE_" + item)));
        permissions.forEach(item -> authorities.add(new SimpleGrantedAuthority(item)));
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}