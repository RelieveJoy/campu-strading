package com.campus.entity;

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
public class LostFound implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 类别：lost=寻物，found=招领 */
    public static final String CATEGORY_LOST = "lost";
    public static final String CATEGORY_FOUND = "found";

    private Long lostFoundId;
    private String title;
    private String description;
    private String imageUrl;
    private String category;    // lost / found
    private String location;    // 在哪见到的
    private String contact;
    private Long userId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
