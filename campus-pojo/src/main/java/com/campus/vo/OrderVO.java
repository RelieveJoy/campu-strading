package com.campus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "订单详情（含商品和买卖家信息）")
public class OrderVO implements Serializable {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "商品ID")
    private Long itemId;

    @Schema(description = "商品标题")
    private String itemTitle;

    @Schema(description = "商品图片URL")
    private String itemImageUrl;

    @Schema(description = "成交金额")
    private BigDecimal amount;

    @Schema(description = "订单状态：1待确认 2已完成 3已取消")
    private Integer status;

    @Schema(description = "买家ID")
    private Long buyerId;

    @Schema(description = "买家姓名")
    private String buyerName;

    @Schema(description = "卖家ID")
    private Long sellerId;

    @Schema(description = "卖家姓名")
    private String sellerName;

    @Schema(description = "下单时间")
    private LocalDateTime createTime;
}
