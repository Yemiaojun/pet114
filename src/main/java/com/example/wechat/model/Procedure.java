package com.example.wechat.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document
public class Procedure {
    @Id
    private ObjectId id;
    private String name;
    private String text;
    private Integer step;
    private List<String> picUrlList;
    private List<String> videoUrlList;
    private Facility facility;
}
