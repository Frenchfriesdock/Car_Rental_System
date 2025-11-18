package com.hosiky.listener;

import com.hosiky.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 取消订单消息的接收者/消费者
 * 监听死信队列，处理超时未支付的订单
 */
@Slf4j
@Component
@RabbitListener(queues = "mall.order.cancel") // 监听死信队列
@RequiredArgsConstructor
public class CancelOrderReceiver {

    private final IOrderService orderService;

    /**
     * 处理接收到的超时订单消息
     */
    @RabbitHandler
    public void handle(Long orderId) {
        try {
            log.info("接收到订单超时取消消息，开始处理: orderId={}", orderId);
            // 调用服务层处理订单取消
            orderService.processOrderTimeout(orderId);
            log.info("订单超时处理完成: orderId={}", orderId);
        } catch (Exception e) {
            log.error("处理订单超时消息异常: orderId={}", orderId, e);
            // 可根据业务需求决定是否抛出异常以触发重试
            throw new RuntimeException("订单取消处理失败", e);
        }
    }
}