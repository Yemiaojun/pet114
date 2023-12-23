package com.example.wechat.model;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class WechatAccountDTO {
    private ObjectId id;
    private int rank;
    private String name;
    private int followers;
    private int totalRead;
    private String domain;
    private String headpic;

}
