package com.hosiky.domain.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SelectRateRuleConditionsDTO {

    @Schema(description = "品牌外键")
    private Integer brandId;

    @Schema(description = "分类外键")
    private Integer categoryId;

    @Schema(description = "0 工作日 1 周末")
    private Integer isWeekend;
}
