package com.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "订单分页查询条件")
public class OrdersPageQueryDTO implements Serializable {

    @Min(value = 1, message = "页码最小为1")
    @Schema(description = "页码")
    private Integer page;

    @Min(value = 1, message = "每页最少1条")
    @Max(value = 50, message = "每页最多50条")
    @Schema(description = "每页条数")
    private Integer pageSize;

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "买家ID")
    private Long buyerId;

    @Schema(description = "卖家ID")
    private Long sellerId;

}
