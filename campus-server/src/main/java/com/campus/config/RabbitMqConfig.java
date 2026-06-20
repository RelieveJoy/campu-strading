package com.campus.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "campus.trading.exchange";
    public static final String QUEUE = "campus.trading.order.notify";
    public static final String ROUTING_KEY = "order.event";

    /** 队列 */
    @Bean
    public Queue orderNotifyQueue() {
        return new Queue(QUEUE, true); // durable=true，重启不丢队列
    }

    /** 交换机 */
    @Bean
    public DirectExchange tradingExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    /** 绑定队列到交换机 */
    @Bean
    public Binding binding(Queue orderNotifyQueue, DirectExchange tradingExchange) {
        return BindingBuilder.bind(orderNotifyQueue)
                .to(tradingExchange)
                .with(ROUTING_KEY);
    }

    /** 消息序列化为 JSON（默认是字节流） */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
