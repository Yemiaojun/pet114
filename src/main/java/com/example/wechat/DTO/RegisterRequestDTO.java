package com.example.wechat.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @ApiModelProperty(value = "用户名", required = true, example = "john_doe")
    private String username;

    @ApiModelProperty(value = "用户密码", required = true, example = "password123")
    private String password;

    @ApiModelProperty(value = "确认密码", required = true, example = "password123")
    private String confirmPassword;

    @ApiModelProperty(value = "用户邮箱", example = "john_doe@example.com")
    private String email;

    @ApiModelProperty(value = "安全问题", example = "Your first pet's name?")
    private String securityQuestion;

    @ApiModelProperty(value = "安全问题答案", example = "Fluffy")
    private String securityQuestionAnswer;
}
