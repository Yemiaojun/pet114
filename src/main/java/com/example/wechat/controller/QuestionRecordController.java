package com.example.wechat.controller;

import com.example.wechat.model.QuestionRecord;
import com.example.wechat.service.QuestionRecordService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.Result;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/questionRecords")
public class QuestionRecordController {

    @Autowired
    private QuestionRecordService questionRecordService;

    @ApiOperation(value = "获取当前用户的问题记录", notes = "返回当前会话userId的所有问题记录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "问题记录获取成功"),
            @ApiResponse(code = 401, message = "用户未登录")
    })
    @GetMapping("/findByUserId")
    public ResponseEntity<String> findQuestionRecordsByUserId(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        List<QuestionRecord> records = questionRecordService.findQuestionRecordsByUserId(userId);
        return ResponseEntity.ok(Result.okGetStringByData("问题记录获取成功", records));
    }


    @ApiOperation(value = "获取指定用户的问题记录", notes = "需要管理员权限，根据提供的用户ID返回其所有问题记录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "问题记录获取成功"),
            @ApiResponse(code = 401, message = "用户未登录"),
            @ApiResponse(code = 403, message = "无权限访问")
    })
    @GetMapping("/findBySpecifiedUserId")
    public ResponseEntity<String> findAllByUserId(
            @ApiParam(value = "指定的用户ID", required = true) @RequestParam String userId,
            HttpSession session) {
        String sessionUserId = (String) session.getAttribute("userId");
        String sessionUserAuth = (String) session.getAttribute("authLevel");

        if (sessionUserId == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        if (!"2".equals(sessionUserAuth)) {
            return ResponseEntity.status(403).body(Result.errorGetString("无权限访问"));
        }

        List<QuestionRecord> records = questionRecordService.findQuestionRecordsByUserId(userId);
        if(records.isEmpty()){
            return ResponseEntity.ok(Result.okGetStringByData("未找到问题记录", null));
        }
        return ResponseEntity.ok(Result.okGetStringByData("问题记录获取成功", records));
    }

    @ApiOperation(value = "获取所有问题记录", notes = "需要管理员权限，返回数据库中的所有问题记录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "所有问题记录获取成功"),
            @ApiResponse(code = 401, message = "用户未登录"),
            @ApiResponse(code = 403, message = "无权限访问")
    })
    @GetMapping("/findAll")
    public ResponseEntity<String> findAllQuestionRecords(HttpSession session) {
        String sessionUserAuth = (String) session.getAttribute("authLevel");

        if (session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        if (!"2".equals(sessionUserAuth)) {
            return ResponseEntity.status(403).body(Result.errorGetString("无权限访问"));
        }

        List<QuestionRecord> records = questionRecordService.findAllQuestionRecords();
        return ResponseEntity.ok(Result.okGetStringByData("所有问题记录获取成功", records));
    }
}
