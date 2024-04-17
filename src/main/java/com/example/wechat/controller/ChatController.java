package com.example.wechat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tool.OpenAIAPI;
import utils.Result;


@RestController
@RequestMapping("/chat")
public class ChatController {

    @GetMapping("/getText")
    public ResponseEntity<String> getText(@RequestParam String send)
    {
        return ResponseEntity.ok(Result.okGetStringByData("回答获取成功","1"));
    }


}
