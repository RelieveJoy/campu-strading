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
public class Favorite implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long favoriteId;
    private Long userId;
    private Long itemId;
    private LocalDateTime createTime;
}
