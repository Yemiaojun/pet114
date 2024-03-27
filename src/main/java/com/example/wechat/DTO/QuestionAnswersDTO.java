package com.example.wechat.DTO;

import com.example.wechat.model.Question;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class QuestionAnswersDTO {

    @ApiModelProperty(value = "用户提交的答案列表", required = true, example = "[\"A\", \"C\", \"B\", \"D\"]")
    private List<String> answerList;

    @ApiModelProperty(value = "相关的问题列表", required = true, notes = "包含完整的Question对象列表")
    private List<Question> questionList;

    @ApiModelProperty(value = "考试的ID", required = true, example = "5f2b5cd942eaff6ac9a87a34")
    private String examId;

}
