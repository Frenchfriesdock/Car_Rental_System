package com.hosiky.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("points_record")
@Schema(description = "积分变动流水")
public class PointsRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户外键")
    private Long userId;

    @Schema(description = "变动积分（正增负减）")
    private Integer points;

    @Schema(description = "来源说明")
    private String remark;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}