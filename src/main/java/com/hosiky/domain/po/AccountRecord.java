package com.hosiky.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("account_record")
@Schema(description = "账户余额流水")
public class AccountRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户外键")
    private Long userId;

    @Schema(description = "1 充值 2 消费 3 退款")
    private Integer type;

    @Schema(description = "变动金额（正增负减）")
    private BigDecimal amount;

    @Schema(description = "变动后余额")
    private BigDecimal balance;

    @Schema(description = "关联订单号")
    private String orderSn;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}