package com.hosiky.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "订单数据传输对象")
public class OrderCreateDTO {

    @Schema(description = "订单ID（更新时必传）")
    private Integer id;

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", required = true)
    private Integer userId;

    @NotNull(message = "车辆ID不能为空")
    @Schema(description = "车辆ID", required = true)
    private Integer carId;

    @NotNull(message = "取车时间不能为空")
    @Schema(description = "计划取车时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  // 关键修复：指定日期格式
    private LocalDateTime pickTime;

    @NotNull(message = "还车时间不能为空")
    @Schema(description = "计划还车时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  // 关键修复：指定日期格式
    private LocalDateTime returnTime;
}