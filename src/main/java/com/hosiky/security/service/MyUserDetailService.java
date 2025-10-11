package com.hosiky.security.service;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.hosiky.domain.po.*;
import com.hosiky.security.entity.MyUserDetail;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * UserDetailsService
 * 这个接口是用来从MySQL中加载用户的信息，然后封装到自定义的UserDetails中
 */
@Component
public class MyUserDetailService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = Db.lambdaQuery(User.class).eq(User::getUsername, username).one();
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();
        List<UserRole> userRoleList = Db.lambdaQuery(UserRole.class).eq(UserRole::getUserId, user.getId()).list();
        for (UserRole userRole : userRoleList) {
            Long roleId = Long.valueOf(userRole.getRoleId());
            // roles
            roles.add(Db.lambdaQuery(Role.class).eq(Role::getId, roleId).one().getName());
            // permission
            List<RolePermission> rolePermissionList = Db.lambdaQuery(RolePermission.class)
                    .eq(RolePermission::getRoleId, roleId).list();
            Db.lambdaQuery(Permission.class)
                    .in(Permission::getId, rolePermissionList.stream().map(RolePermission::getPermissionId).toList())
                    .list()
                    .forEach(permission -> permissions.add(permission.getName()));
        }
        // 去重
        List<String> newPermissions = permissions.stream().distinct().toList();
        return new MyUserDetail(user, roles, newPermissions);
    }
}