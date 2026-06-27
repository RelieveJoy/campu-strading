package com.campus.dto;

import lombok.Data;

@Data
public class LostFoundPageQueryDTO {

    private String keyword;
    private String category;   // lost / found
    private Integer page;
    private Integer pageSize;
}
