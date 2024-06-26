package com.example.wechat.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class ExamRecord {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    @DBRef
    private Exam exam;
    @DBRef
    private User user;
    private String status;
    private Integer score;
}
