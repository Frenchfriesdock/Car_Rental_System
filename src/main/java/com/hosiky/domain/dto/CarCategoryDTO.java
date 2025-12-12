package com.hosiky.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "分类创建或更新请求DTO")
public class CarCategoryDTO {


    @Schema(description = "分类ID，更新时必需")
    private String id; // 更新操作时需要

    @NotBlank(message = "分类名称不能为空")
    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    private String remark;
}
