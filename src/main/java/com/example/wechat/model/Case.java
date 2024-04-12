package com.example.wechat.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
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
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    @DBRef
    private Disease disease;
    private String name;
    private String categoryId;
    private List<String> picUrlList;
    private List<String> videoUrlList;
    private List<String> textList;
    private CaseCheck caseCheck;
    private CasePlan casePlan;
    private CaseResult caseResult;
    private CaseTreat caseTreat;

    @ApiModelProperty(value = "相关文件id", required = true)
    private List<String> files;

    @ApiModelProperty(value = "头像id", required = true)
    private String avatar;
}
