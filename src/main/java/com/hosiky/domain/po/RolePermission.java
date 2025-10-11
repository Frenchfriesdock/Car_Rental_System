package com.hosiky.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("role_permission")
public class RolePermission {
    private Integer roleId;
    private Integer permissionId;
    private Integer deleted;
    private LocalDateTime createdAt;
}