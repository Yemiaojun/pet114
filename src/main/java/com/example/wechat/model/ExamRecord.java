package com.example.wechat.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class ExamRecord {
    @Id
    private ObjectId id;
    private ObjectId examId;
    private ObjectId userId;
    private String status;
    private Integer score;
}
