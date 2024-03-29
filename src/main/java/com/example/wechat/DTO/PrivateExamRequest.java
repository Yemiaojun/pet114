package com.example.wechat.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "创建私人比赛请求信息")
public class PrivateExamRequest {
    @ApiModelProperty(value = "比赛名称", required = true, example = "我的私人比赛")
    private String name;

    @ApiModelProperty(value = "问题ID列表", required = true, notes = "提供问题ID列表用于比赛", example = "[\"5f3a2c1e4a732964bff083fa\", \"5f3a2c3e4a732964bff083fb\"]")
    private List<String> questionIds;

    @ApiModelProperty(value = "开始时间", required = true, example = "2023-01-01T10:00:00.000+00:00")
    private Date startTime;

    @ApiModelProperty(value = "结束时间", required = true, example = "2023-01-01T12:00:00.000+00:00")
    private Date endTime;

    @ApiModelProperty(value = "分数", required = true, example = "100")
    private Integer score;
}
