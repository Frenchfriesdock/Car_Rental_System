package com.hosiky.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "积分流水视图对象")
public class PointsRecordVO {
    
    @Schema(description = "流水ID")
    private Integer id;
    
    @Schema(description = "用户ID")
    private Integer userId;
    
    @Schema(description = "变动积分")
    private Integer points;
    
    @Schema(description = "来源说明")
    private String remark;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}