package com.example.wechat.controller;

import com.example.wechat.DTO.RegisterRequestDTO;
import com.example.wechat.service.FileStorageService;
import io.swagger.annotations.*;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utils.Result;

import java.util.List;
import java.util.Map;
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

    @Autowired
    private FileStorageService fileStorageService;

    @ApiOperation(value="添加用户", notes = "添加新的用户记录（Body)")
    @PostMapping("/addUser")
    public ResponseEntity<String> addUser(@ApiParam(value = "用户信息", required = true) @RequestBody User user) {
        Optional<User> savedUser = userService.addUser(user);
        if (!savedUser.isPresent()) {
            return ResponseEntity.badRequest().body(Result.errorGetString("用户名已存在"));
        }
        user.setPassword(null); // 为了安全，不返回密码信息
        return ResponseEntity.ok(Result.okGetStringByData("用户添加成功", user));
    }

    @ApiOperation(value = "注册新用户", notes = "注册新用户，需要提供用户名、密码、确认密码、电子邮件、安全问题及其答案")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Result.errorGetString("两次输入的密码不一致"));
        }

        if (request.getPassword().length() < 5 || !request.getPassword().matches(".*[a-zA-Z]+.*")) {
            return ResponseEntity.badRequest().body(Result.errorGetString("密码必须至少为5位，并且至少包含一个字母"));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword()); // 在实际应用中应进行加密处理
        user.setEmail(request.getEmail());
        user.setSecurityQuestion(request.getSecurityQuestion());
        user.setSecurityQuestionAnswer(request.getSecurityQuestionAnswer());
        user.setAuth("1"); // 默认权限
        user.setAvatarUrl("\\file\\headpic\\default.png"); // 默认头像

        Optional<User> isUserAdded = userService.addUser(user);
        if (!isUserAdded.isPresent()) {
            return ResponseEntity.badRequest().body(Result.errorGetString("用户名已存在"));
        }

        return ResponseEntity.ok(Result.okGetString("注册成功"));
    }

    @ApiOperation(value = "用户登录", notes = "用户登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/login")
    public ResponseEntity<String> tryLogin(@RequestBody Map<String, String> credentials,
                                           HttpSession session) {

        String username = credentials.get("username");
        String password = credentials.get("password");
        String auth = credentials.get("auth");
        Optional<User> userOptional = userService.tryLogin(username, password,auth);
        if (userOptional.isPresent()) {
            // 登录成功，将用户ID和权限等级保存到会话中
            User user = userOptional.get();
            session.setAttribute("userId", user.getId().toString());
            session.setAttribute("authLevel", user.getAuth()); // 存储权限等级到会话中

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

    @ApiOperation(value = "用户登出", notes = "用户登出接口，结束用户会话")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        // 结束会话
        session.invalidate();

        // 返回登出成功的消息
        return ResponseEntity.ok(Result.okGetString("登出成功"));
    }

    @ApiOperation(value = "根据用户名模糊查找用户", notes = "返回符合条件的用户列表，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取用户信息成功"),
            @ApiResponse(code = 400, message = "用户未登录或不具备更新权限")
    })
    @GetMapping("/searchByUsername")
    public ResponseEntity<String> findUsersByUsername(
            @ApiParam(name = "username", value = "用户名", required = true, example = "john") @RequestParam("username") String username,
            HttpSession session) {
        // 检查用户权限
        String userAuth = (String) session.getAttribute("authLevel");
        if (!"2".equals(userAuth)) {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备查找权限"));
        }

        List<User> users = userService.findUsersByUsernameLike(username);

        return ResponseEntity.ok(Result.okGetStringByData("获取用户信息成功", users));
    }

    @ApiOperation(value = "获取所有用户", notes = "返回所有用户列表，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取所有用户信息成功"),
            @ApiResponse(code = 400, message = "用户未登录或不具备查看权限")
    })
    @GetMapping("/findAllUsers")
    public ResponseEntity<String> findAllUsers(HttpSession session) {
        // 检查用户权限
        String userAuth = (String) session.getAttribute("authLevel");
        if (!"2".equals(userAuth)) {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备查看权限"));
        }

        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(Result.okGetStringByData("获取所有用户信息成功", users));
    }

    @ApiOperation(value = "修改密码", notes = "用户已登陆时，提供当前用户名，密码和新密码来修改密码(body)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "currentPassword", value = "密码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "newPassword", value = "新密码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(
            @RequestBody Map<String, String> payload,
            HttpSession session) {
        String userIdStr = (String) session.getAttribute("userId");
        if (userIdStr == null) {
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }

        // 从请求体中获取username、currentPassword和newPassword
        String username = payload.get("username");
        String currentPassword = payload.get("currentPassword");
        String newPassword = payload.get("newPassword");

        if (newPassword.length() < 5 || !newPassword.matches(".*[a-zA-Z]+.*")) {
            return ResponseEntity.badRequest().body(Result.errorGetString("密码必须至少为5位，并且至少包含一个字母"));
        }

        // 确认会话中的用户ID与提供的用户名匹配，然后尝试修改密码
        Optional<User> userOptional = userService.findUserById(new ObjectId(userIdStr));
        if (userOptional.isPresent() && userOptional.get().getUsername().equals(username) &&
                userService.updatePassword(username, currentPassword, newPassword)) {
            return ResponseEntity.ok(Result.okGetString("密码修改成功"));
        } else {
            return ResponseEntity.badRequest().body(Result.errorGetString("密码修改失败，提供的用户名或密码错误"));
        }
    }

    @ApiOperation(value = "获取密保问题", notes = "根据用户名返回这个用户的密保问题")
    @GetMapping("/getSecurityQuestion")
    public ResponseEntity<String> getSecurityQuestion(@RequestParam String username) {
        String securityQuestion = userService.getSecurityQuestionByUsername(username);
        if (securityQuestion != null) {
            return ResponseEntity.ok(Result.okGetStringByData("密保问题获取成功", securityQuestion));
        } else {
            return ResponseEntity.badRequest().body(Result.errorGetString("找不到对应的用户或用户未设置密保问题"));
        }
    }

    @ApiOperation(value = "根据密保问题答案重置密码", notes = "用户提供用户名、密保答案和新密码来重置密码。请求体应包含username, securityAnswer, newPassword字段。")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "securityAnswer", value = "密码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "newPassword", value = "新密码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body, HttpSession session) {
        String username = body.get("username");
        String securityAnswer = body.get("securityAnswer");
        String newPassword = body.get("newPassword");


        if(username == null || securityAnswer == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Result.errorGetString("请求参数不完整"));
        }

        if (newPassword.length() < 5 || !newPassword.matches(".*[a-zA-Z]+.*")) {
            return ResponseEntity.badRequest().body(Result.errorGetString("密码必须至少为5位，并且至少包含一个字母"));
        }



        boolean updateSuccess = userService.updatePasswordIfSecurityAnswerMatches(username, securityAnswer, newPassword);

        if (updateSuccess) {
            return ResponseEntity.ok(Result.okGetString("密码重置成功"));
        } else {
            return ResponseEntity.badRequest().body(Result.errorGetString("密保问题答案错误或用户不存在"));
        }
    }

    @ApiOperation(value = "根据用户ID修改密码", notes = "管理员根据用户的ID修改密码，需要管理员权限(body)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "newPassword", value = "新密码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/changePasswordById")
    public ResponseEntity<String> changePasswordById(
            @RequestBody Map<String, String> body,
            HttpSession session) {
        // 检查用户权限
        String userAuth = (String) session.getAttribute("authLevel");
        if (!"2".equals(userAuth)) {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备管理员权限"));
        }

        // 从请求体中提取userId和newPassword
        String userId = body.get("userId");
        String newPassword = body.get("newPassword");



        // 检查参数完整性
        if (userId == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Result.errorGetString("请求参数不完整"));
        }

        if (newPassword.length() < 5 || !newPassword.matches(".*[a-zA-Z]+.*")) {
            return ResponseEntity.badRequest().body(Result.errorGetString("密码必须至少为5位，并且至少包含一个字母"));
        }
        // 尝试将字符串ID转换为ObjectId，以便与数据库操作兼容
        try {
            ObjectId objectId = new ObjectId(userId);
            boolean updateSuccess = userService.updatePasswordById(objectId, newPassword);

            if (updateSuccess) {
                return ResponseEntity.ok(Result.okGetString("密码修改成功"));
            } else {
                return ResponseEntity.badRequest().body(Result.errorGetString("用户ID错误或不存在"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.errorGetString("用户ID格式不正确"));
        }
    }

    @ApiOperation(value = "更新用户权限", notes = "根据用户ID更新用户权限，需要管理员权限")
    @PostMapping("/updateAuth")
    public ResponseEntity<String> updateAuth(
            @ApiParam(value = "用户ID", required = true) @RequestParam("id") String userIdStr,
            @ApiParam(value = "新权限等级（1为普通用户，2为管理员）", required = true, example = "1") @RequestParam("auth") String auth,
            HttpSession session) {
        // 检查用户权限
        String userAuth = (String) session.getAttribute("authLevel");
        if (!"2".equals(userAuth)) {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备管理员权限"));
        }

        try {
            ObjectId userId = new ObjectId(userIdStr);
            boolean updateSuccess = userService.updateAuth(userId, auth);

            if (updateSuccess) {
                return ResponseEntity.ok(Result.okGetString("权限更新成功"));
            } else {
                return ResponseEntity.badRequest().body(Result.errorGetString("用户ID错误或不存在"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.errorGetString("用户ID格式不正确"));
        }
    }

    @ApiOperation(value = "上传头像", notes = "用户可以上传一张图片来修改自己的头像，需要用户登录")
    @PostMapping("/uploadAvatar")
    public ResponseEntity<String> uploadAvatar(
            @ApiParam(value = "头像文件", required = true) @RequestParam("file") MultipartFile file,
            HttpSession session) {
        String userIdStr = (String) session.getAttribute("userId");
        if (userIdStr == null) {
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录"));
        }

        try {
            ObjectId userId = new ObjectId(userIdStr);
            User user = userService.findUserById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
            String filePath = fileStorageService.storeAvatar(file, user.getUsername());
            // 假设saveAvatarUrl方法是用来更新用户头像URL的
            userService.saveAvatarUrl(userId, filePath);
            return ResponseEntity.ok(Result.okGetStringByData("头像更新成功", filePath));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.errorGetString("头像更新失败: " + e.getMessage()));
        }
    }

    @ApiOperation(value = "删除用户", notes = "根据用户ID删除用户及相关记录，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "用户删除成功"),
            @ApiResponse(code = 401, message = "用户未登录"),
            @ApiResponse(code = 403, message = "无管理员权限"),
            @ApiResponse(code = 404, message = "用户未找到")
    })
    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId, HttpSession session) {
        String userAuth = (String) session.getAttribute("authLevel");
        if (userAuth == null || !"2".equals(userAuth)) {
            return ResponseEntity.status(403).body(Result.errorGetString("无管理员权限"));
        }

        try {
            userService.deleteUserById(userId);
            return ResponseEntity.ok(Result.okGetString("用户删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }






}
