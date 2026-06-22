package com.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "商品分页查询条件")
public class ItemPageQueryDTO implements Serializable {

    @Min(value = 1, message = "页码最小为1")
    @Schema(description = "页码")
    private Integer page;

    @Min(value = 1, message = "每页最少1条")
    @Max(value = 50, message = "每页最多50条")
    @Schema(description = "每页条数")
    private Integer pageSize;

    @Schema(description = "商品名称关键词")
    private String keyword;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "成色：1全新 2九成新 3七成新 4五成新")
    private Integer itemCondition;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "最低价格")
    private BigDecimal minPrice;

    @Schema(description = "最高价格")
    private BigDecimal maxPrice;

    @Schema(description = "排序：newest/hottest/price_asc/price_desc")
    private String sort;

}
