package com.hosiky.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("brand")
@Schema(description = "品牌字典")
public class Brand {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description = "品牌名称")
    private String name;

//    由于实体类中存在标记了 @TableLogic的字段，MyBatis-Plus 的拦截器会将 DELETE操作自动转换为 UPDATE 操作
    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}