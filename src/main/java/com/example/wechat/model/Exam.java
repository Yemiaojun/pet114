package com.example.wechat.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Data
@Document
public class Exam {
    @Id
    private ObjectId id;
    private String name;
    private List<ObjectId> questionIdList;
    private ObjectId holderId;
    private List<ObjectId> participantList;
    private List<ObjectId> whiteList;
    private Date startTime;
    private Date endTime;
    private String status;
    private Integer score;
}
