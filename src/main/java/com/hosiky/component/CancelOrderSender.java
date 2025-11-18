package com.hosiky.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

/**
 * 取消订单消息的发送者
 * 负责向延迟队列发送消息，实现订单超时自动取消
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CancelOrderSender {

    private final AmqpTemplate amqpTemplate;

    /**
     * 发送延迟取消订单消息
     * CancelOrderSender使用 AmqpTemplate向指定的交换机和路由键发送消息。
     * 通过 MessagePostProcessor设置消息的过期时间（TTL），消息过期后会被自动路由到死信队列
     * @param orderId 订单ID
     * @param delayMinutes 延迟时间（分钟）
     */
    public void send(Long orderId, long delayMinutes) {
        // 将分钟转换为毫秒
        long delayMillis = delayMinutes * 60 * 1000;
        
        // 发送消息到延迟队列
        amqpTemplate.convertAndSend(
            "mall.order.direct.ttl", // 延迟交换机名称
            "mall.order.cancel.ttl", // 延迟路由键
            orderId,
            new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    // 设置消息的过期时间（毫秒）
                    message.getMessageProperties().setExpiration(String.valueOf(delayMillis));
                    return message;
                }
            }
        );
        log.info("发送订单取消延迟消息: orderId={}, 延迟时间={}分钟", orderId, delayMinutes);
    }

    /**
     * 发送默认30分钟延迟消息（重载方法）
     */
    public void send(Long orderId) {
        this.send(orderId, 30); // 默认30分钟超时
    }
}