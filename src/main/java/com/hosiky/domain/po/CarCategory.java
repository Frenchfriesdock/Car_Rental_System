package com.hosiky.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("car_category")
@Schema(description = "车型分类字典")
public class CarCategory {

    @TableId(type = IdType.AUTO)
    private String id;

    @Schema(description = "分类名称")
    private String name;

    @TableLogic
    private Integer deleted;

    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}