package com.example.wechat.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document
public class Department {
    @Id
    private ObjectId id;
    private String name;
    private String info;
    private List<ObjectId> roleList; // Assuming it's a list of role IDs
}
