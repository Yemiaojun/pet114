package com.example.wechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import com.example.wechat.model.Question;
import com.example.wechat.service.QuestionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import utils.Result;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @ApiOperation(value = "添加问题", notes = "添加新的问题，需要管理员权限")
    @PostMapping("/addQuestion")
    public ResponseEntity<String> addQuestion(
            @ApiParam(value = "问题信息", required = true) @RequestBody Question question,
            HttpSession session) {

        String userAuth = (String) session.getAttribute("authLevel");

        if (!"2".equals(userAuth)) {
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备管理员权限"));
        }

        try {
            Question savedQuestion = questionService.addQuestion(question);
            return ResponseEntity.ok(Result.okGetStringByData("问题添加成功", savedQuestion));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.errorGetString("问题添加异常: " + e.getMessage()));
        }
    }
}
