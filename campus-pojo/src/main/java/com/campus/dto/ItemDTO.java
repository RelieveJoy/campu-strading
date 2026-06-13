package com.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "商品新增/编辑时传递的数据模型")
public class ItemDTO implements Serializable {

    @NotBlank(message = "商品标题不能为空")
    @Size(max = 100, message = "商品标题最长100个字符")
    @Schema(description = "商品标题")
    private String title;

    @Size(max = 2000, message = "商品描述最长2000个字符")
    @Schema(description = "商品描述")
    private String description;

    @NotNull(message = "售价不能为空")
    @DecimalMin(value = "0.01", message = "售价必须大于0")
    @DecimalMax(value = "99999999.99", message = "售价超出范围")
    @Digits(integer = 8, fraction = 2, message = "售价最多2位小数")
    @Schema(description = "售价")
    private BigDecimal price;

    @DecimalMin(value = "0", message = "原价不能为负数")
    @DecimalMax(value = "99999999.99", message = "原价超出范围")
    @Digits(integer = 8, fraction = 2, message = "原价最多2位小数")
    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Positive(message = "请选择有效分类")
    @Schema(description = "分类ID")
    private Long categoryId;

    @Size(max = 500, message = "图片URL最长500个字符")
    @Schema(description = "商品图片URL")
    private String imageUrl;

    @Schema(description = "状态：1在售 0下架 2已售出")
    private Integer status;

}
