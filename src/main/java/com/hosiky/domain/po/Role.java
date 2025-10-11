package com.hosiky.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("role")
@Schema(description = "角色字典")
public class Role {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String description;
    private Integer deleted;
    private LocalDateTime createdAt;
}