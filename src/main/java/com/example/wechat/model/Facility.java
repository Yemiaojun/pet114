package com.example.wechat.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Facility {
    @Id
    private ObjectId id;
    private String name;
    private String info;
    private String picUrl;
}
