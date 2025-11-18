package com.hosiky.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Schema(description = "订单视图对象")
public class OrderVO {
    
    @Schema(description = "订单ID")
    private Long id;
    
    @Schema(description = "订单号")
    private String sn;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "车辆ID")
    private Long carId;
    
    @Schema(description = "车辆型号（需要联表查询）")
    private String carModel;
    
    @Schema(description = "车辆品牌（需要联表查询）")
    private String carBrand;
    
    @Schema(description = "租用天数")
    private Integer days;
    
    @Schema(description = "总租金")
    private BigDecimal amount;
    
    @Schema(description = "订单状态码：0-待取车 1-已取车 2-已还车 3-已取消")
    private Integer status;
    
    @Schema(description = "订单状态描述")
    private String statusDesc;
    
    @Schema(description = "计划取车时间")
    private LocalDateTime pickTime;
    
    @Schema(description = "格式化取车时间")
    private String pickTimeFormatted;
    
    @Schema(description = "计划还车时间")
    private LocalDateTime returnTime;
    
    @Schema(description = "格式化还车时间")
    private String returnTimeFormatted;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
    
    @Schema(description = "剩余支付时间（分钟）")
    private Long remainingMinutes;
    
    /**
     * 根据状态码获取状态描述
     */
    public String getStatusDesc() {
        switch (this.status) {
            case 0: return "待取车";
            case 1: return "已取车";
            case 2: return "已还车";
            case 3: return "已取消";
            default: return "未知状态";
        }
    }
    
    /**
     * 获取格式化的取车时间
     */
    public String getPickTimeFormatted() {
        if (this.pickTime == null) return "";
        return this.pickTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
    /**
     * 获取格式化的还车时间
     */
    public String getReturnTimeFormatted() {
        if (this.returnTime == null) return "";
        return this.returnTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
    /**
     * 计算剩余支付时间（用于前端倒计时）
     */
    public Long getRemainingMinutes() {
        if (this.status != 0 || this.createdAt == null) {
            return 0L;
        }
        // 30分钟支付时限
        LocalDateTime expireTime = this.createdAt.plusMinutes(30);
        long remaining = java.time.Duration.between(LocalDateTime.now(), expireTime).toMinutes();
        return Math.max(remaining, 0);
    }
}