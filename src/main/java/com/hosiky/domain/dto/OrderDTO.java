package com.hosiky.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "订单数据传输对象")
public class OrderDTO {
    
    @Schema(description = "订单ID（更新时必传）")
    private Long id;
    
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", required = true)
    private Long userId;
    
    @NotNull(message = "车辆ID不能为空")
    @Schema(description = "车辆ID", required = true)
    private Long carId;
    
    @NotNull(message = "租用天数不能为空")
    @Schema(description = "租用天数", required = true)
    private Integer days;
    
    @NotNull(message = "订单金额不能为空")
    @Schema(description = "总租金", required = true)
    private BigDecimal amount;
    
    @Schema(description = "订单状态：0-待取车 1-已取车 2-已还车 3-已取消")
    private Integer status;
    
    @NotNull(message = "取车时间不能为空")
    @Schema(description = "计划取车时间", required = true)
    private LocalDateTime pickTime;
    
    @NotNull(message = "还车时间不能为空")
    @Schema(description = "计划还车时间", required = true)
    private LocalDateTime returnTime;
}