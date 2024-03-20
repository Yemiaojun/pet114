package com.example.wechat.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document
public class QuestionRecord {
    @Id
    private ObjectId id;
    private ObjectId questionId;
    private String choice;
    private Boolean trueChoice; // Changed 'true' to 'trueChoice' as 'true' is a reserved keyword
    private ObjectId userId;
    private ObjectId examId;
    private Date time;
}
