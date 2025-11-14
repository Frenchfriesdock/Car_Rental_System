package com.hosiky.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "会员等级视图对象")
public class MemberLevelVO {
    
    @Schema(description = "等级ID")
    private Integer id;
    
    @Schema(description = "等级名称")
    private String levelName;
    
    @Schema(description = "成长值下限")
    private Integer minValue;
    
    @Schema(description = "成长值上限")
    private Integer maxValue;
    
    @Schema(description = "租车折扣")
    private BigDecimal discount;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}