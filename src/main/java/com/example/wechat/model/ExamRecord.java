package com.example.wechat.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class ExamRecord {
    @Id
    private ObjectId id;
    @DBRef
    private Exam exam;
    @DBRef
    private User user;
    private Integer score;
}
