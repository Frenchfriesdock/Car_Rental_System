package com.hosiky.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("member_level")
@Schema(description = "会员等级")
public class MemberLevel {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description = "等级名称")
    private String levelName;

    @Schema(description = "成长值下限")
    private Integer minValue;

    @Schema(description = "成长值上限")
    private Integer maxValue;

    @Schema(description = "租车折扣")
    private BigDecimal discount;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}