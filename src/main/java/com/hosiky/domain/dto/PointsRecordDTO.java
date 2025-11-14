package com.hosiky.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "积分流水传输对象")
public class PointsRecordDTO {
    
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", required = true)
    private Long userId;
    
    @NotNull(message = "积分变动值不能为空")
    @Schema(description = "变动积分（正数表示增加，负数表示减少）", required = true)
    private Integer points;
    
    @Schema(description = "来源说明")
    private String remark;
}