package com.example.wechat.model;

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
@ApiModel(description = "问题信息")
public class Question {
    @Id
    private ObjectId id;

    @DBRef
    @ApiModelProperty(value = "关联的类别", notes = "使用MongoDB的DBRef引用Category文档")
    private Category category;

    @ApiModelProperty(value = "正确答案", required = true, example = "A")
    private String answer;

    @ApiModelProperty(value = "题干", required = true, example = "世界上最大的海洋是？")
    private String stem;

    @ApiModelProperty(value = "选项列表", example = "[\"太平洋\", \"大西洋\", \"印度洋\", \"北冰洋\"]")
    private List<String> optionList;

    @ApiModelProperty(value = "分值", example = "5")
    private Integer score;

    @ApiModelProperty(value="可见",example = "true")
    private boolean visible;


}
