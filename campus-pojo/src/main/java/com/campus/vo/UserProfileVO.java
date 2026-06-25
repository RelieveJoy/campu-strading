package com.campus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户公开主页信息")
public class UserProfileVO implements Serializable {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "简介")
    private String bio;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "生日")
    private LocalDate birthday;

    @Schema(description = "在售商品数")
    private Integer totalItems;

    @Schema(description = "已售出商品数")
    private Integer totalSold;

    @Schema(description = "综合评分，无评价时为0")
    private BigDecimal overallRating;

    @Schema(description = "有评分的商品数")
    private Integer ratedItemCount;
}
