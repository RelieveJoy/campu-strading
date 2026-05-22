package com.campus.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "订单详情（含商品和买卖家信息）")
public class OrderVO implements Serializable {

    @ApiModelProperty("订单ID")
    private Long orderId;

    @ApiModelProperty("商品ID")
    private Long itemId;

    @ApiModelProperty("商品标题")
    private String itemTitle;

    @ApiModelProperty("商品图片URL")
    private String itemImageUrl;

    @ApiModelProperty("成交金额")
    private BigDecimal amount;

    @ApiModelProperty("订单状态：1待确认 2已完成 3已取消")
    private Integer status;

    @ApiModelProperty("买家ID")
    private Long buyerId;

    @ApiModelProperty("买家姓名")
    private String buyerName;

    @ApiModelProperty("卖家ID")
    private Long sellerId;

    @ApiModelProperty("卖家姓名")
    private String sellerName;

    @ApiModelProperty("下单时间")
    private LocalDateTime createTime;
}
