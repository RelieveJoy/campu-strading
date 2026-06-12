package com.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "收藏操作时传递的数据模型")
public class FavoriteDTO implements Serializable {

    @Schema(description = "用户ID（由后端从 Token 中获取）")
    private Long userId;

    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID")
    private Long itemId;

}
