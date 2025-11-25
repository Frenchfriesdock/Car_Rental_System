package com.hosiky.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("merchant")
@Schema(description = "车商入驻信息")
public class Merchant {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description = "关联 user.id（车商账号）")
    private Integer userId;

    @Schema(description = "公司全称")
    private String company;

    @Schema(description = "营业执照号码")
    private String licenseNo;

    @Schema(description = "1 正常 0 停用")
    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}