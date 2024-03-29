package com.example.wechat.controller;

import com.example.wechat.DTO.PrivateExamRequest;
import com.example.wechat.DTO.PublicExamRequest;
import com.example.wechat.model.Exam;
import com.example.wechat.service.ExamService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import utils.Result;
@RestController
@RequestMapping("/exams")
public class ExamController {

    @Autowired
    private ExamService examService;

    @ApiOperation(value="创建私人比赛", notes = "允许用户创建一个私人的比赛")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功创建私人比赛"),
            @ApiResponse(code = 401, message = "用户未登录")
    })
    @PostMapping("/holdPrivateExam")
    public ResponseEntity<String> holdPrivateExam(
            @RequestBody PrivateExamRequest privateExamRequest,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        Exam createdExam = examService.holdPrivateExam(
                privateExamRequest.getName(),
                privateExamRequest.getQuestionIds(),
                privateExamRequest.getStartTime(),
                privateExamRequest.getEndTime(),
                privateExamRequest.getScore(),
                userId);

        return ResponseEntity.ok(Result.okGetStringByData("私人比赛创建成功", createdExam));
    }

    @ApiOperation(value = "创建公共比赛", notes = "允许管理员用户创建一个公共的比赛")
    @PostMapping("/holdPublicExam")
    public ResponseEntity<String> holdPublicExam(
            @RequestBody PublicExamRequest publicExamRequest,
            HttpSession session) {
        String userAuthLevel = (String) session.getAttribute("authLevel");

        if (!"2".equals(userAuthLevel)) {
            return ResponseEntity.status(403).body(Result.errorGetString("无权限创建公共比赛"));
        }

        Exam createdExam = examService.holdPublicExam(
                publicExamRequest.getName(),
                publicExamRequest.getQuestionIds(),
                publicExamRequest.getWhiteListUserIds(),
                publicExamRequest.getStartTime(),
                publicExamRequest.getEndTime(),
                publicExamRequest.getScore(),
                (String) session.getAttribute("userId"));

        return ResponseEntity.ok(Result.okGetStringByData("公共比赛创建成功", createdExam));
    }


}
