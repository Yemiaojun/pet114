package com.example.wechat.model;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.Date;

@Data
public class HotPushDTO {
    private ObjectId id;
    private Integer rank;
    private String title;
    private String content; // 文章内容的前20个字

    private String account; // 发布公众号
    private String cover; // 封面 URL
    private String url; // 推送链接

    private Date publicTime; // 发表时间
}
