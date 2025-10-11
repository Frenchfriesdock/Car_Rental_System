package com.hosiky.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_role")
public class UserRole {
    private Long userId;
    private Integer roleId;
    private Integer deleted;
    private LocalDateTime createdAt;
}