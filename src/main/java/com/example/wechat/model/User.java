package com.example.wechat.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class User {
    @Id
    private ObjectId id;
    private String username;
    private String password;
    private String auth;
    private String avatarUrl;
    private String email;
    private String securityQuestion;
    private String securityQuestionAnswer;
}
