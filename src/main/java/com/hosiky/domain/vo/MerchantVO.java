package com.hosiky.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "车商信息返回结果")
public class MerchantVO {

    @Schema(description = "车商ID")
    private Integer id;

    @Schema(description = "关联 user.id（车商账号）")
    private Integer userId;

    @Schema(description = "公司全称")
    private String company;

    @Schema(description = "营业执照号码")
    private String licenseNo;

    @Schema(description = "状态：1 正常 0 停用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "最后更新时间")
    private LocalDateTime updatedAt;

    // 可以额外添加关联信息，比如用户名、电话等
    // @Schema(description = "账号用户名")
    // private String username;
}