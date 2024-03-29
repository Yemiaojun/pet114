package com.example.wechat.service;

import com.example.wechat.model.Exam;
import com.example.wechat.model.Question;
import com.example.wechat.model.User;
import com.example.wechat.repository.ExamRepository;
import com.example.wechat.repository.QuestionRepository;
import com.example.wechat.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    public Exam holdPrivateExam(String name, List<String> questionIds, Date startTime, Date endTime, Integer score, String userId) {
        Exam newExam = new Exam();
        newExam.setName(name);
        List<Question> questionList = convertQuestionIdsToQuestions(questionIds);
        newExam.setQuestionList(questionList);
        newExam.setStartTime(startTime);
        newExam.setEndTime(endTime);
        newExam.setScore(score);
        newExam.setPrivate(true);
        newExam.setEveryone(false);

        // 当前时间判断
        Date now = new Date();
        if (now.before(startTime)) {
            newExam.setStatus("未开始");
        } else if (now.after(startTime) && now.before(endTime)) {
            newExam.setStatus("进行中");
        } else if (now.after(endTime)) {
            newExam.setStatus("已过期");
        } else {
            newExam.setStatus("有效");
        }

        // 设置holder和participantList
        User holder = new User(); // 实际实现时应该是从数据库查询User对象
        holder.setId(new ObjectId(userId));
        newExam.setHolder(holder);
        newExam.setParticipantList(Collections.singletonList(holder)); // 仅包含自己
        newExam.setWhiteList(Collections.singletonList(holder)); // 可选，如果需要白名单

        return examRepository.save(newExam);
    }

    public Exam holdPublicExam(String name, List<String> questionIds, List<String> whiteListUserIds, Date startTime, Date endTime, Integer score, String holderUserId, boolean everyone) {
        Exam newExam = new Exam();
        newExam.setName(name);
        // 转换questionIds到Question的List，逻辑同前
        newExam.setQuestionList(convertQuestionIdsToQuestions(questionIds));
        newExam.setStartTime(startTime);
        newExam.setEndTime(endTime);
        newExam.setScore(score);
        newExam.setPrivate(false);
        newExam.setEveryone(everyone);

        Date now = new Date();
        if (now.before(startTime)) {
            newExam.setStatus("未开始");
        } else if (now.after(startTime) && now.before(endTime)) {
            newExam.setStatus("进行中");
        } else if (now.after(endTime)) {
            newExam.setStatus("已过期");
        } else {
            newExam.setStatus("有效");
        }

        // 设置holder
        User holder = userRepository.findById(new ObjectId(holderUserId)).orElse(null);
        newExam.setHolder(holder);

        // 设置白名单用户
        List<User> whiteList = convertUserIdsToUsers(whiteListUserIds);
        newExam.setWhiteList(whiteList);

        // participantList为空，由用户报名
        return examRepository.save(newExam);
    }

    // 假设的方法，需要你根据实际情况实现
    private List<Question> convertQuestionIdsToQuestions(List<String> questionIds) {
        List<Question> questions = new ArrayList<>();
        for (String questionId : questionIds) {
            Optional<Question> questionOpt = questionRepository.findById(new ObjectId(questionId));
            questionOpt.ifPresent(questions::add);
        }
        return questions;
    }

    public List<User> convertUserIdsToUsers(List<String> userIds) {
        List<User> users = new ArrayList<>();
        for (String userId : userIds) {
            Optional<User> userOpt = userRepository.findById(new ObjectId(userId));
            userOpt.ifPresent(users::add);
        }
        return users;
    }

    public Optional<Exam> findExamById(String id) {
        return examRepository.findById(new ObjectId(id));
    }

    public List<Exam> getExamsByOptionalStatus(String status) {
        if (status == null || status.isEmpty()) {
            return examRepository.findByStatusNot("Deleted");
        } else {
            return examRepository.findByStatus(status);
        }
    }

    public Optional<Exam> setExamStatusToDeleted(String examId) {
        Optional<Exam> examOpt = examRepository.findById(new ObjectId(examId));
        if (examOpt.isPresent()) {
            Exam exam = examOpt.get();
            exam.setStatus("Deleted");
            examRepository.save(exam);
            return Optional.of(exam);
        }
        return Optional.empty();
    }

}
