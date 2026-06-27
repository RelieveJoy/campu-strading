package com.campus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LostFoundDTO {

    @NotBlank(message = "标题不能为空")
    private String title;

    private String description;

    private String imageUrl;

    @NotBlank(message = "类别不能为空")
    private String category;   // lost / found

    private String location;   // 在哪见到的

    private String contact;
}
