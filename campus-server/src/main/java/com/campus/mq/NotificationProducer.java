package com.campus.mq;

import com.campus.config.RabbitMqConfig;
import com.campus.dto.OrderNotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 订单通知生产者：订单状态变更时发送消息到 RabbitMQ。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(OrderNotificationDTO notification) {
        log.info("发送通知: type={}, orderId={}, buyer={}, seller={}",
                notification.getType(), notification.getOrderId(),
                notification.getBuyerName(), notification.getSellerId());
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                RabbitMqConfig.ROUTING_KEY,
                notification);
    }
}
