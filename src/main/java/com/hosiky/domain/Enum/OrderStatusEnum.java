package com.hosiky.domain.Enum;

import lombok.Getter;

/**
 * 订单枚举接口
 */

@Getter
public enum OrderStatusEnum {
    
    PENDING(0, "待取车（待支付）"),
    TAKEN(1, "已取车（已支付）"),
    RETURNED(2, "已还车"),
    CANCELLED(3, "已取消");
    
    private final Integer code;
    private final String desc;
    
    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}