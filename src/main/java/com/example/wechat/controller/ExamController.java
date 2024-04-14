package com.example.wechat.controller;

import com.example.wechat.DTO.PrivateExamRequest;
import com.example.wechat.DTO.PublicExamRequest;
import com.example.wechat.exception.DefaultException;
import com.example.wechat.model.Exam;
import com.example.wechat.model.ExamRecord;
import com.example.wechat.model.Question;
import com.example.wechat.service.ExamRecordService;
import com.example.wechat.service.ExamService;
import com.example.wechat.service.QuestionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

import utils.Result;

@RestController
@RequestMapping("/exams")
public class ExamController {

    @Autowired
    private ExamService examService;

    @Autowired
    private ExamRecordService examRecordService;

    @Autowired
    QuestionService questionService;

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

        List<Question> sessionQuestions = (List<Question>) session.getAttribute("sessionQuestions");
        if (sessionQuestions == null || sessionQuestions.isEmpty()) {
            return ResponseEntity.badRequest().body(Result.errorGetString("会话中没有题目，无法创建考试"));
        }

        List<String> questionIds = sessionQuestions.stream().map(Question::getId).map(ObjectId::toString).collect(Collectors.toList());
        Exam createdExam = examService.holdPrivateExam(
                privateExamRequest.getName(),
                questionIds,
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


        try{
            List<Question> sessionQuestions = (List<Question>) session.getAttribute("sessionQuestions");
            if (sessionQuestions == null || sessionQuestions.isEmpty()) {
                return ResponseEntity.badRequest().body(Result.errorGetString("会话中没有题目，无法创建考试"));
            }
            List<String> questionIds = sessionQuestions.stream().map(Question::getId).map(ObjectId::toString).collect(Collectors.toList());
            Exam createdExam = examService.holdPublicExam(
                publicExamRequest.getName(),
                questionIds,
                publicExamRequest.getWhiteListUserIds(),
                publicExamRequest.getStartTime(),
                publicExamRequest.getEndTime(),
                publicExamRequest.getScore(),
                (String) session.getAttribute("userId"),
                publicExamRequest.getEveryone()
                );
            return ResponseEntity.ok(Result.okGetStringByData("公共比赛创建成功", createdExam));
        }catch (Exception e){
            return ResponseEntity.status(400).body(e.getMessage());
        }



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
            @ApiParam(value = "考试ID", required = true) @RequestBody Map<String, String> payload,
            HttpSession session) {
        String examId = payload.get("examId");
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

    @ApiOperation(value = "模糊搜索考试", notes = "用户（非管理员）根据考试名进行模糊搜索，并可根据考试的公私性、状态及可参与性进行筛选。")
    @ApiResponses({
            @ApiResponse(code = 200, message = "搜索成功"),
            @ApiResponse(code = 401, message = "用户未登录")
    })
    @GetMapping("/search")
    public ResponseEntity<String> searchExams(
            @ApiParam(value = "考试名称", required = true) @RequestParam String name,
            @ApiParam(value = "考试状态", required = false) @RequestParam(required = false) String status,
            @ApiParam(value = "是否为私人考试", required = false) @RequestParam(required = false) Boolean Private,
            @ApiParam(value = "是否可参与", required = false) @RequestParam(required = false) Boolean participatable,
            HttpSession session) {
        String userIdStr = (String) session.getAttribute("userId");
        if (userIdStr == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }
        ObjectId userId = new ObjectId(userIdStr);

        List<Exam> exams = examService.searchExams(name, userId, status, Private, participatable);
        if (exams.isEmpty()) {
            return ResponseEntity.ok(Result.okGetStringByData("没有找到符合条件的考试", exams));
        } else {
            return ResponseEntity.ok(Result.okGetStringByData("搜索成功", exams));
        }
    }

    @ApiOperation(value = "管理员模糊搜索考试", notes = "管理员根据考试名进行模糊搜索，并可根据考试的公私性及状态进行筛选。")
    @ApiResponses({
            @ApiResponse(code = 200, message = "搜索成功"),
            @ApiResponse(code = 401, message = "用户未登录"),
            @ApiResponse(code = 403, message = "无管理员权限")
    })
    @GetMapping("/adminSearch")
    public ResponseEntity<String> adminSearchExams(
            @ApiParam(value = "考试名称", required = true) @RequestParam String name,
            @ApiParam(value = "考试状态", required = false) @RequestParam(required = false) String status,
            @ApiParam(value = "是否为私人考试", required = false) @RequestParam(required = false) Boolean isPrivate,
            HttpSession session) {
        String userAuth = (String) session.getAttribute("authLevel");

        // 检查是否为管理员
        if (!"2".equals(userAuth)) {
            return ResponseEntity.status(403).body(Result.errorGetString("无管理员权限"));
        }

        List<Exam> exams = examService.adminSearchExams(name, status, isPrivate);
        if (exams.isEmpty()) {
            return ResponseEntity.ok(Result.okGetStringByData("没有找到符合条件的考试", exams));
        } else {
            return ResponseEntity.ok(Result.okGetStringByData("搜索成功", exams));
        }
    }

    @ApiOperation(value = "参加考试", notes = "将当前用户添加到某个考试的participantList中，并创建考试记录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功参加考试"),
            @ApiResponse(code = 401, message = "用户未登录"),
            @ApiResponse(code = 404, message = "考试或用户未找到")
    })
    @PostMapping("/joinExam")
    public ResponseEntity<String> joinExam(
            @ApiParam(value = "考试ID", required = true) @RequestParam String examId,
            HttpSession session) {

        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        try {
            examService.joinExam(examId, userId);
            return ResponseEntity.ok(Result.okGetString("成功参加考试"));
        } catch (DefaultException e) {
            return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
        }
    }

    @ApiOperation(value = "添加用户到白名单", notes = "添加指定用户到考试的白名单")
    @ApiResponses({
            @ApiResponse(code = 200, message = "用户成功添加到白名单"),
            @ApiResponse(code = 401, message = "用户未登录"),
            @ApiResponse(code = 403, message = "无管理员权限"),
            @ApiResponse(code = 404, message = "考试或用户未找到")
    })
    @PostMapping("/addToWhitelist")
    public ResponseEntity<String> addUserToWhitelist(
            @ApiParam(value = "考试ID", required = true) @RequestParam String examId,
            @ApiParam(value = "用户ID", required = true) @RequestParam String userId,
            HttpSession session) {

        String userAuth = (String) session.getAttribute("authLevel");

        if (!"2".equals(userAuth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Result.errorGetString("无管理员权限"));
        }

        try {
            examService.addUserToWhitelist(examId, userId);
            return ResponseEntity.ok(Result.okGetString("用户成功添加到白名单"));
        } catch (DefaultException e) {
            return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
        }
    }

    @ApiOperation(value = "从白名单中删除用户", notes = "从指定考试的白名单中移除指定用户")
    @ApiResponses({
            @ApiResponse(code = 200, message = "用户成功从白名单中移除"),
            @ApiResponse(code = 401, message = "用户未登录"),
            @ApiResponse(code = 403, message = "无管理员权限"),
            @ApiResponse(code = 404, message = "考试或用户未找到")
    })
    @PostMapping("/removeFromWhitelist")
    public ResponseEntity<String> removeUserFromWhitelist(
            @ApiParam(value = "考试ID", required = true) @RequestParam String examId,
            @ApiParam(value = "用户ID", required = true) @RequestParam String userId,
            HttpSession session) {

        String userAuth = (String) session.getAttribute("authLevel");

        if (!"2".equals(userAuth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Result.errorGetString("无管理员权限"));
        }

        try {
            examService.removeUserFromWhitelist(examId, userId);
            return ResponseEntity.ok(Result.okGetString("用户成功从白名单中移除"));
        } catch (DefaultException e) {
            return ResponseEntity.badRequest().body(Result.errorGetString(e.getMessage()));
        }
    }

    @ApiOperation(value = "获取考试记录", notes = "根据考试ID获取所有考试记录，并可指定排序方式（score）和状态")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取考试记录成功"),
            @ApiResponse(code = 401, message = "用户未登录"),
            @ApiResponse(code = 404, message = "考试记录未找到")
    })
    @GetMapping("/getExamRecordsByExamId")
    public ResponseEntity<String> getExamRecordsByExamId(
            @ApiParam(value = "考试ID", required = true) @RequestParam String examId,
            @ApiParam(value = "排序方式", required = false) @RequestParam(required = false) String sort,
            @ApiParam(value = "状态", required = false) @RequestParam(required = false) String status,
            HttpSession session) {

        if (session.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.errorGetString("用户未登录"));
        }

        List<ExamRecord> records = examRecordService.findExamRecordsByExamId(examId, sort, status);
        if (records.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.errorGetString("考试记录未找到"));
        } else {
            return ResponseEntity.ok(Result.okGetStringByData("获取考试记录成功", records));
        }
    }



    @ApiOperation(value = "获取对应id的所有Question", notes = "根据考试ID获取所有考试记录，并可指定排序方式,如果排序方式为score则按分数降序")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取考试记录成功"),
            @ApiResponse(code = 401, message = "用户未登录"),
            @ApiResponse(code = 404, message = "考试记录未找到")
    })
    @GetMapping("/getQuestionsOf")
    public ResponseEntity<String> getQuestionsByExamId(
            @ApiParam(value = "考试ID", required = true) @RequestParam String examId,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.errorGetString("用户未登录"));
        }

        List<Question> questions = examService.findQuestionByExamId(examId);

        return ResponseEntity.ok(Result.okGetStringByData("获取考题成功", questions));


    }

    @ApiOperation(value = "获取用户的考试记录", notes = "根据用户ID获取该用户的所有考试记录，并可按分数和状态排序")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取考试记录成功"),
            @ApiResponse(code = 401, message = "用户未登录"),
            @ApiResponse(code = 404, message = "考试记录未找到")
    })
    @GetMapping("/getExamRecordsByUserId")
    public ResponseEntity<String> getExamRecordsByUserId(
            @ApiParam(value = "用户ID", required = true) @RequestParam String userId,
            @ApiParam(value = "排序方式", required = false) @RequestParam(required = false, defaultValue = "none") String sort,
            @ApiParam(value = "状态", required = false) @RequestParam(required = false) String status,
            HttpSession session) {

        if (session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        List<ExamRecord> records = examRecordService.findExamRecordsByUserId(userId, sort, status);
        if (records.isEmpty()) {
            return ResponseEntity.status(404).body(Result.errorGetString("考试记录未找到"));
        } else {
            return ResponseEntity.ok(Result.okGetStringByData("获取考试记录成功", records));
        }
    }

    @ApiOperation(value = "获取单个考试记录详情", notes = "根据考试记录ID获取具体的考试记录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取考试记录详情成功"),
            @ApiResponse(code = 401, message = "用户未登录"),
            @ApiResponse(code = 404, message = "考试记录未找到")
    })
    @GetMapping("/getExamRecordById")
    public ResponseEntity<String> getExamRecordById(
            @ApiParam(value = "考试记录ID", required = true) @RequestParam String recordId,
            HttpSession session) {

        // 检查用户登录状态
        if (session.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.errorGetString("用户未登录"));
        }

        Optional<ExamRecord> recordOpt = examRecordService.findExamRecordsById(recordId);
        if (recordOpt.isPresent()) {
            return ResponseEntity.ok(Result.okGetStringByData("获取考试记录详情成功", recordOpt.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.errorGetString("考试记录未找到"));
        }
    }

    @ApiOperation(value = "获取用户的考试记录", notes = "根据用户ID获取该用户的所有考试记录，并可按分数和状态排序")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取考试记录成功"),
            @ApiResponse(code = 401, message = "用户未登录"),
            @ApiResponse(code = 404, message = "考试记录未找到")
    })
    @GetMapping("/getMyRecords")
    public ResponseEntity<String> getMyRecords(
            @ApiParam(value = "排序方式", required = false) @RequestParam(required = false, defaultValue = "none") String sort,
            @ApiParam(value = "状态", required = false) @RequestParam(required = false) String status,
            HttpSession session) {


        if (session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }
        String userId= (String) session.getAttribute("userId");
        List<ExamRecord> records = examRecordService.findExamRecordsByUserId(userId, sort, status);
        if (records.isEmpty()) {
            return ResponseEntity.status(404).body(Result.errorGetString("考试记录未找到"));
        } else {
            return ResponseEntity.ok(Result.okGetStringByData("获取考试记录成功", records));
        }
    }

    @ApiOperation(value = "检查用户是否是考试参加者", notes = "检查当前会话的用户是否在指定考试的参加者列表中")
    @ApiResponses({
            @ApiResponse(code = 200, message = "检查完成"),
            @ApiResponse(code = 401, message = "用户未登录"),
            @ApiResponse(code = 404, message = "考试未找到")
    })
    @GetMapping("/{examId}/checkParticipant")
    public ResponseEntity<String> checkIfUserIsParticipant(
            @ApiParam(value = "考试ID", required = true) @PathVariable String examId,
            HttpSession session) {

        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.errorGetString("用户未登录"));
        }

        boolean isParticipant = examService.isUserAParticipant(examId, userId);
        if (isParticipant) {
            return ResponseEntity.ok(Result.okGetStringByData("用户是参加者", true));
        } else {
            return ResponseEntity.ok(Result.okGetStringByData("用户不是参加者", false));
        }
    }

    @ApiOperation(value = "添加题目到会话列表", notes = "将选定的题目添加到当前会话的题目列表中")
    @PostMapping("/addQuestionToSession")
    public ResponseEntity<String> addQuestionToSession(
            @ApiParam(value = "题目ID", required = true) @RequestParam String questionId,
            HttpSession session) {


        Question question = questionService.findQuestionById(questionId).orElse(null);
        if (question == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.errorGetString("题目未找到"));
        }

        List<Question> sessionQuestions = (List<Question>) session.getAttribute("sessionQuestions");
        if (sessionQuestions == null) {
            sessionQuestions = new ArrayList<>();
        }
        sessionQuestions.add(question);
        session.setAttribute("sessionQuestions", sessionQuestions);

        return ResponseEntity.ok(Result.okGetString("题目已添加到会话"));
    }

    @ApiOperation(value = "获取会话中的题目列表", notes = "返回当前会话中存储的所有题目")
    @GetMapping("/getSessionQuestions")
    public ResponseEntity<String> getSessionQuestions(HttpSession session) {
        List<Question> sessionQuestions = (List<Question>) session.getAttribute("sessionQuestions");
        if (sessionQuestions == null || sessionQuestions.isEmpty()) {
            return ResponseEntity.ok(Result.okGetStringByData("会话中没有题目", Collections.emptyList()));
        }
        return ResponseEntity.ok(Result.okGetStringByData("获取会话中的题目成功", sessionQuestions));
    }

    @ApiOperation(value = "清空会话中的题目列表", notes = "从当前会话中移除所有已添加的题目")
    @PostMapping("/clearSessionQuestions")
    public ResponseEntity<String> clearSessionQuestions(HttpSession session) {
        session.removeAttribute("sessionQuestions");
        return ResponseEntity.ok(Result.okGetString("会话中的题目列表已清空"));
    }















}
