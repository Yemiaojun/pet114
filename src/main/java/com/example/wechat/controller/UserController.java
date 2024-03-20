package com.example.wechat.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;
import utils.Result;

import java.util.List;
import java.util.Optional;

import com.example.wechat.model.User;
import com.example.wechat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/users")

public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation(value="添加用户", notes = "添加新的用户记录（Body)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "用户密码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "auth", value = "用户权限", required = true, dataType = "String"),
            @ApiImplicitParam(name = "avatarUrl", value = "用户头像URL", required = false, dataType = "String"),
            @ApiImplicitParam(name = "email", value = "用户邮箱", required = false, dataType = "String"),
            @ApiImplicitParam(name = "securityQuestion", value = "安全问题", required = false, dataType = "String"),
            @ApiImplicitParam(name = "securityQuestionAnswer", value = "安全问题答案", required = false, dataType = "String")
    })
    @PostMapping("/addUser")
    public ResponseEntity<String> addUser(@RequestBody User user) {
        Optional<User> savedUser = userService.addUser(user);
        if (!savedUser.isPresent()) {
            // 用户名已存在的情况
            return ResponseEntity.badRequest().body(Result.errorGetString("用户名已存在"));
        }
        // 用户添加成功
        return ResponseEntity.ok(Result.okGetStringByData("用户添加成功", savedUser.get()));
    }


    @ApiOperation(value = "用户登录", notes = "用户登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/login")
    public ResponseEntity<String> tryLogin(@RequestParam("username") String username,
                                           @RequestParam("password") String password,
                                           HttpSession session) {
        Optional<User> userOptional = userService.tryLogin(username, password);
        if (userOptional.isPresent()) {
            // 登录成功，将用户ID和权限等级保存到会话中
            User user = userOptional.get();
            session.setAttribute("userId", user.getId().toString());
            session.setAttribute("auth", user.getAuth()); // 存储权限等级到会话中

            // 为了安全起见，返回的用户信息不应包含敏感信息如密码
            user.setPassword(null);

            return ResponseEntity.ok(Result.okGetStringByData("登录成功", user));
        } else {
            // 登录失败
            return ResponseEntity.badRequest().body(Result.errorGetString("用户名或密码错误"));
        }
    }



    @ApiOperation(value = "获取当前用户信息", notes = "返回当前会话中的用户信息")
    @GetMapping("/current")
    public ResponseEntity<String> getCurrentUser(HttpSession session) {
        // 从会话中获取用户ID
        String userIdStr = (String) session.getAttribute("userId");
        if (userIdStr != null && !userIdStr.isEmpty()) {
            try {
                // 将字符串ID转换为ObjectId
                ObjectId userId = new ObjectId(userIdStr);
                // 使用服务层方法根据ID查找用户
                User user = userService.findUserById(userId).orElse(null);
                if (user != null) {
                    // 处理密码字段，为了安全不返回给前端
                    user.setPassword(null); // 或者设置为其他不敏感的值
                    return ResponseEntity.ok(Result.okGetStringByData("获取用户信息成功", user));
                } else {
                    return ResponseEntity.badRequest().body(Result.errorGetString("无法找到用户信息"));
                }
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Result.errorGetString("用户ID格式不正确"));
            }
        } else {
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或会话已过期"));
        }
    }
}
