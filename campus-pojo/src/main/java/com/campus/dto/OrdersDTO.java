package com.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "创建订单时传递的数据模型")
public class OrdersDTO implements Serializable {

    @Schema(description = "商品ID")
    private Long itemId;

}
