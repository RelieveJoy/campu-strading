package com.campus.mq;

import com.campus.config.RabbitMqConfig;
import com.campus.dto.OrderNotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * 订单通知消费者：接收 RabbitMQ 消息，通过 WebSocket 推送给相关用户。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = RabbitMqConfig.QUEUE)
    public void handle(OrderNotificationDTO notification) {
        log.info("收到通知: type={}, orderId={}, buyer={}, seller={}, item={}",
                notification.getType(), notification.getOrderId(),
                notification.getBuyerName(), notification.getSellerId(),
                notification.getItemTitle());

        // 推送给买卖双方各自的通知频道
        messagingTemplate.convertAndSend(
                "/topic/notify." + notification.getBuyerId(), notification);
        messagingTemplate.convertAndSend(
                "/topic/notify." + notification.getSellerId(), notification);
    }
}
