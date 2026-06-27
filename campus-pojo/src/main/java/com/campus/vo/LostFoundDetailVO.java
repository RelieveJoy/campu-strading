package com.campus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "失物招领详情")
public class LostFoundDetailVO implements Serializable {

    @Schema(description = "ID")
    private Long lostFoundId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "图片URL")
    private String imageUrl;

    @Schema(description = "类别 lost=寻物 found=招领")
    private String category;

    @Schema(description = "在哪见到的")
    private String location;

    @Schema(description = "联系方式")
    private String contact;

    @Schema(description = "发布者ID")
    private Long userId;

    @Schema(description = "发布者姓名")
    private String userName;

    @Schema(description = "发布时间")
    private LocalDateTime createTime;
}
