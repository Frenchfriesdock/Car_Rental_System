package com.hosiky.config;


import com.hosiky.domain.Enum.QueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    /**
     * 延迟交换机，队列，绑定
     * @return
     */
    @Bean
    public DirectExchange ttlExchange() {
        return new DirectExchange(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getExchange(), true, false);
    }

    /**
     * 创建一个直接交换机，用于接受延迟消息
     * @return
     */
    @Bean
    public Queue ttlQueue() {
        return QueueBuilder.durable(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getName())
                .withArgument("x-dead-letter-exchange", QueueEnum.QUEUE_ORDER_CANCEL.getExchange())
                .withArgument("x-dead-letter-routing-key", QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey())
                .build();
    }

    /**
     * 消息的绑定
     * @return
     */
    @Bean
    public Binding ttlBinding() {
        return BindingBuilder.bind(ttlQueue()).to(ttlExchange())
                .with(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getRouteKey());
    }

    /**
     * 真正的消费队列
     */
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(QueueEnum.QUEUE_ORDER_CANCEL.getExchange(), true, false);
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QueueEnum.QUEUE_ORDER_CANCEL.getName()).build();
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange())
                .with(QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey());
    }
}