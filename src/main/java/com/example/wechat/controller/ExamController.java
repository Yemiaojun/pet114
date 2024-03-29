package com.example.wechat.controller;

import com.example.wechat.DTO.PrivateExamRequest;
import com.example.wechat.DTO.PublicExamRequest;
import com.example.wechat.model.Exam;
import com.example.wechat.service.ExamService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import utils.Result;
import java.util.Optional;
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
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功创建公共比赛"),
            @ApiResponse(code = 403, message = "用户无登录")
    })
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
                (String) session.getAttribute("userId"),
                publicExamRequest.getEveryone()
                );


        return ResponseEntity.ok(Result.okGetStringByData("公共比赛创建成功", createdExam));
    }

    @ApiOperation(value = "根据ID获取考试详情", notes = "根据考试的ID返回考试详情")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取考试信息成功"),
            @ApiResponse(code = 401, message = "用户未登录"),
            @ApiResponse(code = 404, message = "考试未找到")
    })
    @GetMapping("/getExamById")
    public ResponseEntity<String> getExamById(
            @ApiParam(value = "考试ID", required = true) @RequestParam String id,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        Optional<Exam> examOpt = examService.findExamById(id);
        if (examOpt.isPresent()) {
            return ResponseEntity.ok(Result.okGetStringByData("获取考试信息成功", examOpt.get()));
        } else {
            return ResponseEntity.status(404).body(Result.errorGetString("考试未找到"));
        }
    }

    @ApiOperation(value = "获取考试列表", notes = "返回所有考试的列表，可根据状态进行过滤。状态为： 未开始 进行中 已过期 Deleted，如果没有指定，则默认返回所有非deleted的exam")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取考试列表成功"),
            @ApiResponse(code = 401, message = "用户未登录")
    })
    @GetMapping("/list")
    public ResponseEntity<String> getExams(
            @ApiParam(value = "考试状态", required = false) @RequestParam(required = false) String status,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        List<Exam> exams = examService.getExamsByOptionalStatus(status);
        if (exams.isEmpty()) {
            return ResponseEntity.ok(Result.okGetStringByData("没有找到考试", null));
        } else {
            return ResponseEntity.ok(Result.okGetStringByData("获取考试列表成功", exams));
        }
    }

    @ApiOperation(value = "设置考试状态为Deleted", notes = "根据考试ID设置考试状态为Deleted，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "考试状态更新成功"),
            @ApiResponse(code = 401, message = "用户未登录"),
            @ApiResponse(code = 403, message = "无管理员权限"),
            @ApiResponse(code = 404, message = "考试未找到")
    })
    @PostMapping("/setExamStatusToDeleted")
    public ResponseEntity<String> setExamStatusToDeleted(
            @ApiParam(value = "考试ID", required = true) @RequestParam String examId,
            HttpSession session) {
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");

        if (userIdStr == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        } else if (!"2".equals(userAuth)) {
            return ResponseEntity.status(403).body(Result.errorGetString("无管理员权限"));
        }

        Optional<Exam> examOpt = examService.setExamStatusToDeleted(examId);
        if (examOpt.isPresent()) {
            return ResponseEntity.ok(Result.okGetStringByData("考试状态更新成功", examOpt.get()));
        } else {
            return ResponseEntity.status(404).body(Result.errorGetString("考试未找到"));
        }
    }







}
