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

    private List<String> picUrlList;
    private List<String> videoUrlList;

    private Role role;

    //pirvate Interger step;
    //缺了一个量，忘写了，因为要和role连在一起。ss
}
