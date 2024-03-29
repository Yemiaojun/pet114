package com.example.wechat.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "创建公共比赛请求信息")
public class PublicExamRequest {
    @ApiModelProperty(value = "比赛名称", required = true, example = "公共挑战赛")
    private String name;

    @ApiModelProperty(value = "问题ID列表", required = true, notes = "提供问题ID列表用于比赛", example = "[\"6038b2a2f2b3e7123456d1f8\", \"6038b2aaf2b3e7123456d1f9\"]")
    private List<String> questionIds;

    @ApiModelProperty(value = "白名单用户ID列表", notes = "提供白名单用户ID列表用于比赛", example = "[\"6038b3b2f2b3e7123456d1fa\", \"6038b3baf2b3e7123456d1fb\"]")
    private List<String> whiteListUserIds;

    @ApiModelProperty(value = "开始时间", required = true, example = "2023-01-01T10:00:00.000+00:00")
    private Date startTime;

    @ApiModelProperty(value = "结束时间", required = true, example = "2023-01-01T12:00:00.000+00:00")
    private Date endTime;

    @ApiModelProperty(value = "分数", required = true, example = "100")
    private Integer score;

    @ApiModelProperty(value = "是否所有人都可以参加", required = true, example = "true")
    private boolean everyone;

    public boolean getEveryone() {
        return everyone;
    }
}
