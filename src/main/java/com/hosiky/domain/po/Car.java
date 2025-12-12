package com.hosiky.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("car")
@Schema(description = "车辆档案")
public class Car {

    @TableId(type = IdType.AUTO)
    private String id;

    @Schema(description = "车牌号")
    private String plateNo;

    @Schema(description = "具体型号")
    private String model;

    @Schema(description = "品牌外键")
    private String brandId;

    @Schema(description = "分类外键")
    private String categoryId;

    @Schema(description = "车商外键（NULL=平台自营）")
    private String merchantId;

    @Schema(description = "兜底日价（无费率规则时用）")
    private BigDecimal priceDaily;

    @Schema(description = "1 空闲 2 出租中 3 维修")
    private Integer status;

    @Schema(description = "车辆照片URL")
    private String imgUrl;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}