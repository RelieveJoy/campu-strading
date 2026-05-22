package com.campus.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "订单分页查询条件")
public class OrdersPageQueryDTO implements Serializable {

    @ApiModelProperty("页码")
    private Integer page;

    @ApiModelProperty("每页条数")
    private Integer pageSize;

    @ApiModelProperty("订单状态")
    private Integer status;

    @ApiModelProperty("买家ID")
    private Long buyerId;

    @ApiModelProperty("卖家ID")
    private Long sellerId;

}
