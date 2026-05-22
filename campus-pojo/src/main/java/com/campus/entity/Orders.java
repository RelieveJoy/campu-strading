package com.campus.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orders implements Serializable {

    /**
     * 订单状态 1待确认 2已完成 3已取消
     */
    public static final Integer TO_BE_CONFIRMED = 1;
    public static final Integer COMPLETED = 2;
    public static final Integer CANCELLED = 3;

    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Long itemId;
    private Long buyerId;
    private Long sellerId;
    private BigDecimal amount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser;
    private Long updateUser;
}
