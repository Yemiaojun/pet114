package com.example.wechat.controller;

import com.example.wechat.DTO.QuestionAnswersDTO;
import com.example.wechat.DTO.QuestionImportDTO;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import com.example.wechat.model.Question;
import com.example.wechat.service.QuestionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import utils.Result;

import java.util.List;
import java.util.Optional;

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

    @ApiOperation(value = "更新问题信息", notes = "根据提供的问题信息更新问题，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "问题更新成功"),
            @ApiResponse(code = 400, message = "问题更新失败，问题可能不存在或用户未登录/无权限")
    })
    @PutMapping("/updateQuestion")
    public ResponseEntity<String> updateQuestion(@ApiParam(value = "问题信息", required = true) @RequestBody Question question, HttpSession session) {
        String userIdStr = (String) session.getAttribute("userId");
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if (userIdStr != null && "2".equals(userAuth)) {
            try{
                Optional<Question> updatedQuestion = questionService.updateQuestion(question);
                if (updatedQuestion.isPresent()) {
                    return ResponseEntity.ok(Result.okGetStringByData("问题更新成功", updatedQuestion.get()));
                } else {
                    return ResponseEntity.badRequest().body(Result.errorGetString("问题更新失败，问题不存在"));
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Result.errorGetString("问题更新异常: " + e.getMessage()));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备更新权限"));
        }
    }

    @ApiOperation(value = "隐藏问题", notes = "将指定的问题设置为不可见，需要管理员权限")
    @ApiResponses({
            @ApiResponse(code = 200, message = "问题隐藏成功"),
            @ApiResponse(code = 400, message = "问题隐藏失败，问题可能不存在或用户未登录/无权限")
    })
    @PutMapping("/hideQuestion")
    public ResponseEntity<String> hideQuestion(@ApiParam(value = "问题ID", required = true) @RequestParam String questionId, HttpSession session) {
        String userAuth = (String) session.getAttribute("authLevel");

        // 确认用户已登录且具有管理员权限
        if ("2".equals(userAuth)) {
            Optional<Question> hiddenQuestion = questionService.hideQuestion(questionId);
            if (hiddenQuestion.isPresent()) {
                return ResponseEntity.ok(Result.okGetString("问题隐藏成功"));
            } else {
                return ResponseEntity.badRequest().body(Result.errorGetString("问题隐藏失败，问题不存在"));
            }
        } else {
            // 用户未登录或不具备管理员权限
            return ResponseEntity.badRequest().body(Result.errorGetString("用户未登录或不具备隐藏权限"));
        }
    }

    @ApiOperation(value = "获取所有问题", notes = "返回所有问题列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取所有问题信息成功"),
            @ApiResponse(code = 401, message = "用户未登录")
    })
    @GetMapping("/findAllQuestions")
    public ResponseEntity<String> findAllQuestions(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        List<Question> questions = questionService.findAllQuestions();
        return ResponseEntity.ok(Result.okGetStringByData("获取所有问题信息成功", questions));
    }

    @ApiOperation(value = "获取所有可见问题", notes = "返回所有可见问题的列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取所有可见问题信息成功"),
            @ApiResponse(code = 401, message = "用户未登录")
    })
    @GetMapping("/findAllVisibleQuestions")
    public ResponseEntity<String> findAllVisibleQuestions(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        List<Question> questions = questionService.findAllVisibleQuestions();
        return ResponseEntity.ok(Result.okGetStringByData("获取所有可见问题信息成功", questions));
    }

    @ApiOperation(value = "根据类别ID获取问题", notes = "返回引用了指定类别ID的所有问题列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取问题列表成功"),
            @ApiResponse(code = 401, message = "用户未登录")
    })
    @GetMapping("/findByCategoryId")
    public ResponseEntity<String> findByCategoryId(
            @ApiParam(value = "类别ID", required = true) @RequestParam String categoryIdStr,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        try {
            ObjectId categoryId = new ObjectId(categoryIdStr);
            List<Question> questions = questionService.findQuestionsByCategoryId(categoryId);
            return ResponseEntity.ok(Result.okGetStringByData("获取问题列表成功", questions));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.errorGetString("无效的类别ID"));
        }
    }

    @ApiOperation(value = "根据类别名称获取问题", notes = "返回引用了指定类别名称的所有问题列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取问题列表成功"),
            @ApiResponse(code = 401, message = "用户未登录")
    })
    @GetMapping("/findByCategoryName")
    public ResponseEntity<String> findByCategoryName(
            @ApiParam(value = "类别名称", required = true) @RequestParam String categoryName,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        List<Question> questions = questionService.findQuestionsByCategoryName(categoryName);
        return ResponseEntity.ok(Result.okGetStringByData("获取问题列表成功", questions));
    }

    @ApiOperation(value = "根据ID获取问题", notes = "根据问题的ID返回问题详情")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取问题信息成功"),
            @ApiResponse(code = 401, message = "用户未登录"),
            @ApiResponse(code = 404, message = "问题未找到")
    })
    @GetMapping("/findQuestionById")
    public ResponseEntity<String> findQuestionById(
            @ApiParam(value = "问题ID", required = true) @RequestParam String questionId,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        Optional<Question> questionOpt = questionService.findQuestionById(questionId);
        if (questionOpt.isPresent()) {
            return ResponseEntity.ok(Result.okGetStringByData("获取问题信息成功", questionOpt.get()));
        } else {
            return ResponseEntity.status(404).body(Result.errorGetString("问题未找到"));
        }
    }

    @ApiOperation(value = "根据题面模糊查找问题", notes = "根据题面模糊查找题目")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查找问题成功"),
            @ApiResponse(code = 401, message = "用户未登录")
    })
    @GetMapping("/findQuestionByStem")
    public ResponseEntity<String> findQuestionByStem(
            @ApiParam(value = "题面", required = true) @RequestParam String stem,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        List<Question> questions = questionService.findQuestionsByStemLike(stem);
        return ResponseEntity.ok(Result.okGetStringByData("查找问题成功", questions));
    }

    @ApiOperation(value = "根据类别ID获取可见问题", notes = "返回引用了指定类别ID且可见的所有问题列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取可见问题列表成功"),
            @ApiResponse(code = 401, message = "用户未登录")
    })
    @GetMapping("/findVisibleQuestionsByCategoryId")
    public ResponseEntity<String> findVisibleQuestionsByCategoryId(
            @ApiParam(value = "类别ID", required = true) @RequestParam String categoryIdStr,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        try {
            ObjectId categoryId = new ObjectId(categoryIdStr);
            List<Question> questions = questionService.findVisibleQuestionsByCategoryId(categoryId);
            return ResponseEntity.ok(Result.okGetStringByData("获取可见问题列表成功", questions));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.errorGetString("无效的类别ID"));
        }
    }

    @ApiOperation(value = "根据类别名称获取可见问题", notes = "返回引用了指定类别名称且可见的所有问题列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取可见问题列表成功"),
            @ApiResponse(code = 401, message = "用户未登录")
    })
    @GetMapping("/findVisibleQuestionsByCategoryName")
    public ResponseEntity<String> findVisibleQuestionsByCategoryName(
            @ApiParam(value = "类别名称", required = true) @RequestParam String categoryName,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        List<Question> questions = questionService.findVisibleQuestionsByCategoryName(categoryName);
        return ResponseEntity.ok(Result.okGetStringByData("获取可见问题列表成功", questions));
    }

    @ApiOperation(value = "根据题面模糊查找可见问题", notes = "根据题面模糊查找题目，仅返回可见问题")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查找问题成功"),
            @ApiResponse(code = 401, message = "用户未登录")
    })
    @GetMapping("/findVisibleQuestionsByStem")
    public ResponseEntity<String> findVisibleQuestionsByStem(
            @ApiParam(value = "题面", required = true) @RequestParam String stem,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        List<Question> questions = questionService.findVisibleQuestionsByStemLike(stem);
        return ResponseEntity.ok(Result.okGetStringByData("查找可见问题成功", questions));
    }

    @ApiOperation(value = "随机抽取n道题", notes = "可以传入categoryid进行筛选，也可以不传入从所有题目中抽取")
    @ApiResponses({
            @ApiResponse(code = 200, message = "随机题目抽取成功"),
            @ApiResponse(code = 401, message = "用户未登录")
    })
    @GetMapping("/getRandomQuestions")
    public ResponseEntity<String> getRandomQuestions(
            @ApiParam(value = "数量n", required = true) @RequestParam Integer n,
            @ApiParam(value = "类别ID（可选）") @RequestParam(required = false) String categoryId,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        List<Question> questions = questionService.getRandomQuestions(n, categoryId);
        return ResponseEntity.ok(Result.okGetStringByData("随机题目抽取成功", questions));
    }

    @ApiOperation(value = "判断答案正确，并生成questionRecords", notes = "传入答案串和题目串，比对答案并生成记录。")
    @ApiResponses({
            @ApiResponse(code = 200, message = "答案校验完成，记录已生成"),
            @ApiResponse(code = 401, message = "用户未登录")
    })
    @PostMapping("/checkQuestionAnswers")
    public ResponseEntity<String> checkQuestionAnswers(
            @ApiParam(value = "答案和题目列表", required = true) @RequestBody QuestionAnswersDTO questionAnswersDTO,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Result.errorGetString("用户未登录"));
        }

        questionService.checkQuestionAnswers(questionAnswersDTO.getAnswerList(), questionAnswersDTO.getQuestionList(), questionAnswersDTO.getExamId(), userId);
        return ResponseEntity.ok(Result.okGetString("答案校验完成，记录已生成"));
    }

    @PostMapping("/importQuestions")
    public ResponseEntity<String> importQuestions(
            HttpSession session) {
        String userAuth = (String) session.getAttribute("authLevel");

        if (!"2".equals(userAuth)) {
            return ResponseEntity.status(402).body(Result.errorGetString("用户未登录或没有权限"));
        }

        try {
            questionService.importQuestions();
            return ResponseEntity.ok(Result.okGetString("导入完成"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body("题目导入失败: " + e.getMessage());
        }
    }




}
