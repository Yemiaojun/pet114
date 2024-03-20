package com.example.wechat.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document
public class Case {
    @Id
    private ObjectId id;
    @DBRef
    private Disease disease;
    private String name;
    private List<String> picUrlList;
    private List<String> videoUrlList;
    private List<String> textList;
}
