package com.hosiky.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.math.BigDecimal;

@Data
@Schema(description = "会员等级传输对象")
public class MemberLevelDTO {
    
    @Schema(description = "等级ID")
    private Integer id;
    
    @NotBlank(message = "等级名称不能为空")
    @Schema(description = "等级名称", required = true)
    private String levelName;
    
    @NotNull(message = "成长值下限不能为空")
    @Schema(description = "成长值下限", required = true)
    private Integer minValue;
    
    @NotNull(message = "成长值上限不能为空")
    @Schema(description = "成长值上限", required = true)
    private Integer maxValue;
    
    @NotNull(message = "租车折扣不能为空")
    @DecimalMin(value = "0.01", message = "折扣必须大于0")
    @DecimalMax(value = "1.00", message = "折扣不能大于1")
    @Schema(description = "租车折扣", required = true)
    private BigDecimal discount;
}