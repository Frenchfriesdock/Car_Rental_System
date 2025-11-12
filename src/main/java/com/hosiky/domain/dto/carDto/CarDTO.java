package com.hosiky.domain.dto.carDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "车辆数据传输对象")
public class CarDTO {
    
    @Schema(description = "车辆ID（更新时必填，新增时不需要）")
    private Integer id;
    
    @NotBlank(message = "车牌号不能为空")
    @Pattern(regexp = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-Z0-9]{4,5}[A-Z0-9挂学警港澳]$",
             message = "车牌号格式不正确")
    @Schema(description = "车牌号", example = "京A12345")
    private String plateNo;
    
    @NotBlank(message = "车型号不能为空")
    @Schema(description = "具体型号", example = "Model S")
    private String model;
    
    @NotNull(message = "品牌ID不能为空")
    @Positive(message = "品牌ID必须为正数")
    @Schema(description = "品牌ID", example = "1")
    private Integer brandId;
    
    @NotNull(message = "分类ID不能为空")
    @Positive(message = "分类ID必须为正数")
    @Schema(description = "分类ID", example = "1")
    private Integer categoryId;
    
    @Schema(description = "车商ID（NULL=平台自营）", example = "1")
    private Long merchantId;
    
    @NotNull(message = "日租金不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "日租金必须大于0")
    @Digits(integer = 6, fraction = 2, message = "日租金格式不正确")
    @Schema(description = "兜底日价", example = "299.99")
    private BigDecimal priceDaily;
    
    @Schema(description = "车辆状态：1-空闲, 2-出租中, 3-维修", example = "1")
    private Integer status;
    
    @Schema(description = "车辆照片URL")
    private String imgUrl;
}