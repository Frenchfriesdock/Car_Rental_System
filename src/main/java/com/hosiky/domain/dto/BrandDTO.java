package com.hosiky.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "品牌创建或更新请求DTO")
public class BrandDTO {

    @Schema(description = "品牌ID，更新时必需")
    private Integer id; // 更新操作时需要

    @NotBlank(message = "品牌名称不能为空")
    @Schema(description = "品牌名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    // 注意：DTO 通常不包含 createdAt, updatedAt, deleted 等字段
    // 这些字段应由后端自动处理
}