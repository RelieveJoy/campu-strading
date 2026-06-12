package com.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "创建订单时传递的数据模型")
public class OrdersDTO implements Serializable {

    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID")
    private Long itemId;

}
