package com.hosiky.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("`order`")   // 注意关键字
@Schema(description = "租车订单")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "业务单号")
    private String sn;

    @Schema(description = "租车用户外键")
    private Long userId;

    @Schema(description = "车辆外键")
    private Integer carId;

    @Schema(description = "租用天数")
    private Integer days;

    @Schema(description = "总租金（已含工作日/周末差异）")
    private BigDecimal amount;

    @Schema(description = "0 待取车 1 已取车 2 已还车 3 已取消")
    private Integer status;

    @Schema(description = "取车时间")
    private LocalDateTime pickTime;

    @Schema(description = "还车时间")
    private LocalDateTime returnTime;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}