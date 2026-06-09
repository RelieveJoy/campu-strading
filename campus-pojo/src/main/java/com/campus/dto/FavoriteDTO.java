package com.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "收藏操作时传递的数据模型")
public class FavoriteDTO implements Serializable {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "商品ID")
    private Long itemId;

}
