package com.example.wechat.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Data
@Document
public class Exam {
    @Id
    private ObjectId id;
    private String name;
    @DBRef
    private List<Question> questionList;
    @DBRef
    private User holder;
    @DBRef
    private List<User> participantList;
    @DBRef
    private List<User> whiteList;
    private Date startTime;
    private Date endTime;
    private String status;
    private Integer score;
    private boolean Private;
}
