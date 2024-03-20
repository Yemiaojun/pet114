package com.example.wechat.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document
public class Question {
    @Id
    private ObjectId id;
    @DBRef
    private Category category;
    private String answer;
    private String stem;
    private List<String> optionList;
    private Integer score;
}
