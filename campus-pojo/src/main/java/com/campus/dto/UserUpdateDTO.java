package com.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户修改个人信息时传递的数据模型")
public class UserUpdateDTO implements Serializable {

    @Schema(description = "昵称")
    private String username;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像URL")
    private String avatar;
}
