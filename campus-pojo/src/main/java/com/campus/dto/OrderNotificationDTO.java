package com.campus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单通知消息体，通过 RabbitMQ 发送。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotificationDTO implements Serializable {

    /** 订单ID */
    private Long orderId;
    /** 商品ID */
    private Long itemId;
    /** 商品标题 */
    private String itemTitle;
    /** 成交价 */
    private BigDecimal amount;
    /** 买家ID */
    private Long buyerId;
    /** 买家名 */
    private String buyerName;
    /** 卖家ID */
    private Long sellerId;
    /** 卖家名 */
    private String sellerName;
    /** 触发操作的发起者ID */
    private Long triggerUserId;
    /** 触发操作的发起者名 */
    private String triggerUserName;
    /** 通知类型：CREATED / CONFIRMED / CANCELLED */
    private String type;
}
