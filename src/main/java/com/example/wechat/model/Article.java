    package com.example.wechat.model;

    import lombok.Data;
    import org.springframework.data.annotation.Id;
    import org.springframework.data.mongodb.core.mapping.Document;
    import org.bson.types.ObjectId;
    import java.util.Date;
    @Data
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
        private String cover;//封面url
        private boolean visit;//是否已经访问过
        private int heat;


    }
