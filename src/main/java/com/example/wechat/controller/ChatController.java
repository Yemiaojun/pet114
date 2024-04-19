package com.example.wechat.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tool.OpenAIAPI;
import utils.Result;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private HttpSession session;

    // Map to store the last chat time for each user
    private static final Map<String, Long> lastChatTimes = new ConcurrentHashMap<>();

    @ApiOperation(value = "与AI对话", notes = "用户通过发送一段文本来与AI进行交互，每个用户20秒内只能调用一次")
    @PostMapping("/interact")
    public ResponseEntity<String> interactWithAI(@RequestBody Map<String, String> request) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("用户未登录");
        }

        // Check if the user has chatted within the last 20 seconds
        Long lastChatTime = lastChatTimes.get(userId);
        long currentTime = System.currentTimeMillis();
        if (lastChatTime != null && (currentTime - lastChatTime) < 20000) {
            return ResponseEntity.status(402).body("请求过于频繁，请稍后再试");
        }

        // Get the text to chat about from the request body
        String text = request.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.status(400).body("请求内容不能为空");
        }

        try {
            String response = OpenAIAPI.chat(text, userId);
            lastChatTimes.put(userId, currentTime); // Update last chat time
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("内部服务器错误: " + e.getMessage());
        }
    }


}
