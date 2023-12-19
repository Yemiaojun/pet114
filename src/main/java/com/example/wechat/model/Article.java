package com.example.wechat.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;
import java.util.Date;

@Document
public class Article {
    @Id
    private ObjectId id; // 自动生成的MongoDB ObjectId
    private String title; // 文章标题
    private String content; // 文章内容
    private String writer; // 文章作者（公众号名称）
    private String link; // 文章链接
    private int likes; // 点赞数
    private int read; // 阅读量
    private int words; // 文章字数
    private Date time; // 发布时间

    // 构造函数、getter和setter
}
