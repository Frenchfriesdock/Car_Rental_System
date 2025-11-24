package com.hosiky.domain.vo.carVo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "车辆视图对象")
public class CarVO implements Serializable {
    
    @Schema(description = "车辆ID")
    private Integer id;
    
    @Schema(description = "车牌号")
    private String plateNo;
    
    @Schema(description = "具体型号")
    private String model;
    
    @Schema(description = "品牌ID")
    private Integer brandId;
    
    @Schema(description = "品牌名称")
    private String brandName;
    
    @Schema(description = "分类ID")
    private Integer categoryId;
    
    @Schema(description = "分类名称")
    private String categoryName;
    
    @Schema(description = "车商ID")
    private Long merchantId;
    
    @Schema(description = "车商名称")
    private String merchantName;
    
    @Schema(description = "是否平台自营")
    private Boolean isPlatformOwned;
    
    @Schema(description = "兜底日价")
    private BigDecimal priceDaily;
    
    @Schema(description = "车辆状态：1-空闲, 2-出租中, 3-维修")
    private Integer status;
    
    @Schema(description = "状态描述")
    private String statusDesc;
    
    @Schema(description = "车辆照片URL")
    private String imgUrl;

    /**
     * 获取状态描述
     */
    public String getStatusDesc() {
        switch (this.status) {
            case 1: return "空闲";
            case 2: return "出租中";
            case 3: return "维修中";
            default: return "未知状态";
        }
    }
    
    /**
     * 判断是否平台自营
     */
    public Boolean getIsPlatformOwned() {
        return this.merchantId == null;
    }
}