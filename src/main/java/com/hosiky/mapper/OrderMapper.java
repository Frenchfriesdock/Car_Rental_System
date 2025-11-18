package com.hosiky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hosiky.domain.po.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Update("UPDATE `order` SET status = #{status}, updated_at = NOW() WHERE id = #{orderId} AND deleted = 0")
    int updateOrderStatus(Long id, int i);

    /**
     * 取消订单：仅能取消待取车状态的订单
     */
    @Update("UPDATE `order` SET status = 3, updated_at = NOW() WHERE id = #{orderId} AND status = 0 AND deleted = 0")
    int cancelOrder(Long orderId);

    /**
     * 还车：更新订单状态为“已还车”
     */
    @Update("UPDATE `order` SET status = 2, updated_at = NOW() WHERE id = #{orderId} AND status = 1 AND deleted = 0")
    int confirmReturn(Long orderId);
}
