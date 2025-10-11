package com.hosiky.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName(value = "user", autoResultMap = true)
@Schema(description = "用户主表")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "用户主键")
    private Long id;

    @Schema(description = "登录账号")
    private String username;

    @Schema(description = "bcrypt密文")
    private String password;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "生日")
    private LocalDate birthday;

    @Schema(description = "账户余额")
    private BigDecimal balance;

    @Schema(description = "积分余额")
    private Integer points;

    @Schema(description = "成长值")
    private Integer growValue;

    @Schema(description = "1 正常 0 禁用")
    private Integer status;

    @TableLogic
    @TableField("deleted")
    @Schema(description = "逻辑删除：0 未删 1 已删")
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}