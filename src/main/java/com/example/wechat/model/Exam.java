package com.example.wechat.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
    @JsonSerialize(using = ToStringSerializer.class)
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
    private boolean everyone;
}
