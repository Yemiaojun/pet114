package com.example.wechat.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Data
@Document
@ApiModel(description = "用户信息")
public class User {
    @Id
    private ObjectId id;

    @ApiModelProperty(value = "用户名", required = true, example = "john_doe")
    private String username;

    @ApiModelProperty(value = "用户密码", required = true, example = "password123")
    private String password;

    @ApiModelProperty(value = "用户权限", required = true, example = "2")
    private String auth;

    @ApiModelProperty(value = "用户头像URL", example = "http://example.com/avatar.jpg")
    private String avatarUrl;

    @ApiModelProperty(value = "用户邮箱", example = "john_doe@example.com")
    private String email;

    @ApiModelProperty(value = "安全问题", example = "Your first pet's name?")
    private String securityQuestion;

    @ApiModelProperty(value = "安全问题答案", example = "Fluffy")
    private String securityQuestionAnswer;
}
