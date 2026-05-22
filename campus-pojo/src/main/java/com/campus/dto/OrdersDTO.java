package com.campus.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "创建订单时传递的数据模型")
public class OrdersDTO implements Serializable {

    @ApiModelProperty("商品ID")
    private Long itemId;

}
