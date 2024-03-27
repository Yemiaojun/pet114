package com.example.wechat.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document
public class QuestionRecord {
    @Id
    private ObjectId id;
    @DBRef
    private Question question;
    private String choice;
    private Boolean TorF;
    @DBRef
    private User user;
    @DBRef
    private Exam exam;
    private Date time;
}
