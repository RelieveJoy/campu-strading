package com.campus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户修改个人信息时传递的数据模型")
public class UserUpdateDTO implements Serializable {

    @Size(max = 50, message = "姓名最长50个字符")
    @Schema(description = "昵称")
    private String username;

    @Size(max = 20, message = "手机号最长20个字符")
    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;

    @Size(max = 255, message = "头像URL最长255个字符")
    @Schema(description = "头像URL")
    private String avatar;
}
