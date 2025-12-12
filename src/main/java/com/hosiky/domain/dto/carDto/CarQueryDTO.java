package com.hosiky.domain.dto.carDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "车辆查询参数")
public class CarQueryDTO {
    
    @Schema(description = "车牌号（模糊查询）")
    private String plateNo;
    
    @Schema(description = "品牌ID")
    private String brandId;
    
    @Schema(description = "分类ID")
    private String categoryId;
    
    @Schema(description = "车商ID")
    private String merchantId;
    
    @Schema(description = "车辆状态：1-空闲, 2-出租中, 3-维修")
    private String status;
    
    @Schema(description = "最低日租金")
    private Double minPriceDaily;
    
    @Schema(description = "最高日租金")
    private Double maxPriceDaily;
    
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;
    
    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}