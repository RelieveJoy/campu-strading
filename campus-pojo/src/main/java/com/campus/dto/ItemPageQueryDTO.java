package com.campus.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(description = "商品分页查询条件")
public class ItemPageQueryDTO implements Serializable {

    @ApiModelProperty("页码")
    private Integer page;

    @ApiModelProperty("每页条数")
    private Integer pageSize;

    @ApiModelProperty("商品名称关键词")
    private String keyword;

    @ApiModelProperty("分类ID")
    private Long categoryId;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("最低价格")
    private BigDecimal minPrice;

    @ApiModelProperty("最高价格")
    private BigDecimal maxPrice;

}
