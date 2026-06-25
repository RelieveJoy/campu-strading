package com.campus.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewDTO {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "商品ID不能为空")
    private Long itemId;

    @NotNull(message = "评分不能为空")
    @Min(value = 0, message = "评分最小为0")
    @Max(value = 5, message = "评分最大为5")
    private Integer rating;

    private String content;
}
