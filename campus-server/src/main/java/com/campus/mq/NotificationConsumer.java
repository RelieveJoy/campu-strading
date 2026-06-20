package com.campus.mq;

import com.campus.config.RabbitMqConfig;
import com.campus.dto.OrderNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 订单通知消费者：接收 RabbitMQ 消息。
 * 目前输出日志，后续可扩展为 WebSocket 推送、短信/邮件通知等。
 */
@Component
@Slf4j
public class NotificationConsumer {

    @RabbitListener(queues = RabbitMqConfig.QUEUE)
    public void handle(OrderNotificationDTO notification) {
        log.info("收到通知: type={}, orderId={}, buyer={}, seller={}, item={}",
                notification.getType(), notification.getOrderId(),
                notification.getBuyerName(), notification.getSellerId(),
                notification.getItemTitle());

        // TODO: 后续接入 WebSocket 实时推送、消息落库等
    }
}
