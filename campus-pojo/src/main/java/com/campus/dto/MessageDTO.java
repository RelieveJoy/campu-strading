package com.campus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class MessageDTO implements Serializable {

    @NotNull(message = "商品ID不能为空")
    private Long itemId;

    @NotNull(message = "接收者不能为空")
    private Long receiverId;

    @NotBlank(message = "消息内容不能为空")
    private String content;
}
