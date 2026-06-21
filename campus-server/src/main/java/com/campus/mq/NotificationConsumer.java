package com.campus.mq;

import com.campus.config.RabbitMqConfig;
import com.campus.dto.OrderNotificationDTO;
import com.campus.entity.Notification;
import com.campus.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 订单通知消费者：先入库（保证离线用户也能看到），再通过 WebSocket 实时推送。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = RabbitMqConfig.QUEUE)
    public void handle(OrderNotificationDTO dto) {
        log.info("收到通知: type={}, orderId={}, buyer={}, seller={}",
                dto.getType(), dto.getOrderId(), dto.getBuyerName(), dto.getSellerId());

        String buyerMsg = buildContent(dto, "买家");
        String sellerMsg = buildContent(dto, "卖家");

        // 入库——离线用户重新上线后能查到
        // 给买家插入
        notificationMapper.insert(Notification.builder()
                .userId(dto.getBuyerId())
                .initiatorId(dto.getTriggerUserId())
                .initiatorName(dto.getTriggerUserName())
                .type(dto.getType())
                .orderId(dto.getOrderId())
                .itemTitle(dto.getItemTitle())
                .content(buyerMsg)
                .createTime(LocalDateTime.now())
                .build());
        // 给卖家插入
        notificationMapper.insert(Notification.builder()
                .userId(dto.getSellerId())
                .initiatorId(dto.getTriggerUserId())
                .initiatorName(dto.getTriggerUserName())
                .type(dto.getType())
                .orderId(dto.getOrderId())
                .itemTitle(dto.getItemTitle())
                .content(sellerMsg)
                .createTime(LocalDateTime.now())
                .build());

        // 实时推送——在线用户即时看到
        messagingTemplate.convertAndSend(
                "/topic/notify." + dto.getBuyerId(), buildNotifyPayload(buyerMsg, dto));
        messagingTemplate.convertAndSend(
                "/topic/notify." + dto.getSellerId(), buildNotifyPayload(sellerMsg, dto));
    }

    private String buildContent(OrderNotificationDTO dto, String role) {
        boolean isBuyer = "买家".equals(role);
        boolean iAmTrigger = dto.getTriggerUserId().equals(isBuyer ? dto.getBuyerId() : dto.getSellerId());
        String triggerName = dto.getTriggerUserName();

        return switch (dto.getType()) {
            case "CREATED" -> isBuyer
                    ? "你已下单「" + dto.getItemTitle() + "」¥" + dto.getAmount() + "，等待卖家确认"
                    : "买家 " + dto.getBuyerName() + " 购买了你的「" + dto.getItemTitle() + "」¥" + dto.getAmount() + "，请尽快处理";
            case "CONFIRMED" -> isBuyer
                    ? "卖家已确认完成订单「" + dto.getItemTitle() + "」"
                    : "订单「" + dto.getItemTitle() + "」已确认完成";
            case "CANCELLED" -> iAmTrigger
                    ? "你已取消订单「" + dto.getItemTitle() + "」"
                    : triggerName + " 取消了订单「" + dto.getItemTitle() + "」";
            default -> "订单状态更新";
        };
    }

    private Map<String, Object> buildNotifyPayload(String content, OrderNotificationDTO dto) {
        return Map.of("type", dto.getType(),
                      "orderId", dto.getOrderId(),
                      "itemTitle", dto.getItemTitle() != null ? dto.getItemTitle() : "",
                      "content", content);
    }
}
