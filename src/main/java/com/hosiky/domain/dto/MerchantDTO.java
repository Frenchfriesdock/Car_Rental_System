package com.hosiky.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "创建或更新车商请求")
public class MerchantDTO {

    @Schema(description = "车商ID，更新时必需")
    private Long id;

    @NotNull(message = "关联用户ID不能为空")
    @Schema(description = "关联 user.id（车商账号）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotBlank(message = "公司全称不能为空")
    @Schema(description = "公司全称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String company;

    @NotBlank(message = "营业执照号码不能为空")
    @Schema(description = "营业执照号码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String licenseNo;

    @Schema(description = "状态：1 正常 0 停用", defaultValue = "1")
    private Integer status = 1; // 设置默认值

    // 注意：不包含 createdAt, updatedAt, deleted
}