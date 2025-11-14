
package com.hosiky.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "费率规则视图对象")
public class RateRuleVO {
    
    @Schema(description = "费率规则ID")
    private Integer id;
    
    @Schema(description = "品牌ID")
    private Integer brandId;
    
    @Schema(description = "分类ID")
    private Integer categoryId;
    
    @Schema(description = "日期类型：0 工作日 1 周末")
    private Integer isWeekend;
    
    @Schema(description = "基础价格（元/天）")
    private BigDecimal basePrice;
    
    @Schema(description = "浮动系数")
    private BigDecimal ratio;
    
    @Schema(description = "逻辑删除标记")
    private Integer deleted;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
    
    // 可以添加关联信息
    @Schema(description = "品牌名称")
    private String brandName;
    
    @Schema(description = "分类名称")
    private String categoryName;
    
    @Schema(description = "日期类型描述")
    public String getIsWeekendDesc() {
        return isWeekend != null && isWeekend == 1 ? "周末" : "工作日";
    }
    
    @Schema(description = "计算后的每日价格")
    public BigDecimal getDailyPrice() {
        if (basePrice == null || ratio == null) {
            return BigDecimal.ZERO;
        }
        return basePrice.multiply(ratio).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}