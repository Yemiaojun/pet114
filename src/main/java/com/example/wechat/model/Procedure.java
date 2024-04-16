package com.example.wechat.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private Integer index;
    private String info;

    private String name;

    private List<String> picUrlList;
    private List<String> videoUrlList;



    @DBRef
    private Activity activity;


    @ApiModelProperty(value = "相关文件id", required = true)
    private List<String> files;

    @ApiModelProperty(value = "头像id", required = true)
    private String avatar;
}
