package com.example.wechat.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@Data
@Document
@ApiModel(description = "药品信息")
public class Drug {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @ApiModelProperty(notes = "药品名称", example = "阿司匹林", required = true)
    private String name;

    @ApiModelProperty(notes = "药品信息", example = "用于缓解轻微疼痛", required = true)
    private String info;

    @ApiModelProperty(notes = "药品图片URL", example = "http://example.com/aspirin.jpg")
    private String picUrl;

    @ApiModelProperty(value = "相关文件id", required = true)
    @DBRef
    private List<String> files;

    @ApiModelProperty(value = "头像id", required = true)
    @DBRef
    private String avatar;
}
