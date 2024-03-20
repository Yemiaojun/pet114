package com.example.wechat.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Disease {
    @Id
    private ObjectId id;
    private String name;
    @DBRef
    private Category category;
    private String text;
}
