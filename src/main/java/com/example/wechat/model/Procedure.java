package com.example.wechat.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document
@ApiModel(description = "扮演过程")
public class Procedure {
    @Id
    private ObjectId id;
    private Integer index;
    private String text;

    private List<String> picUrlList;
    private List<String> videoUrlList;

    @DBRef
    private Role role;
}
