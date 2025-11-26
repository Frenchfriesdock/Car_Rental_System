package com.hosiky.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "工作日/周末费率规则")
public class RateRuleDTO {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description = "品牌外键")
    private Integer brandId;

    @Schema(description = "分类外键")
    private Integer categoryId;

    @Schema(description = "0 工作日 1 周末")
    private Integer isWeekend;

    @Schema(description = "起步价(元/天)")
    private BigDecimal basePrice;

    @Schema(description = "浮动系数")
    private BigDecimal ratio;
}
