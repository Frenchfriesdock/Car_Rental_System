package com.hosiky.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("permission")
@Schema(description = "权限点字典")
public class Permission {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private String name;
    private Integer deleted;
    private LocalDateTime createdAt;
}