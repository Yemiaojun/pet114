package com.example.wechat.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Data
@Document
@ApiModel(description = "角色信息")
public class Role {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @ApiModelProperty(value = "角色名称", required = true, example = "实习医师")
    private String name;

    @ApiModelProperty(value = "角色描述", example = "在宠物医院实习的一般医师")
    private String text;
}
