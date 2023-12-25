package com.example.wechat.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;
@Data
@Document
public class WechatAccount {
    @Id
    private ObjectId id; // 自动生成的MongoDB ObjectId
    private String name; // 公众号名称
    private Integer followers; // 关注者数量
    private Integer totalRead; // 每周总阅读量
    private String domain; // 领域（例如美妆、宠物等）
    private Integer heat;
    private String headpic;//链接

}
