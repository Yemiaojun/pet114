package com.example.wechat.service;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.model.Exam;
import com.example.wechat.model.ExamRecord;
import com.example.wechat.model.Question;
import com.example.wechat.model.User;
import com.example.wechat.repository.ExamRecordRepository;
import com.example.wechat.repository.ExamRepository;
import com.example.wechat.repository.QuestionRepository;
import com.example.wechat.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

    @Autowired
    private ExamRecordRepository examRecordRepository;

    private final MongoTemplate mongoTemplate;
    public ExamService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Exam holdPrivateExam(String name, List<String> questionIds, Date startTime, Date endTime, Integer score, String userId) {
        if (name == null || questionIds == null || startTime == null || endTime == null || score == null || userId == null) {
            throw new DefaultException("参数不完整");
        }
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

    public Exam holdPublicExam(String name, List<String> questionIds, List<String> whiteListUserIds, Date startTime, Date endTime, Integer score, String holderUserId, Boolean everyone) {
        if (name == null || questionIds == null || startTime == null || endTime == null || score == null || holderUserId == null || everyone == null) {
            throw new DefaultException("参数不完整");
        }
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

    public List<Exam> searchExams(String name, ObjectId userId, String status, Boolean Private, Boolean participatable) {
        Query query = new Query();

        // 名称模糊搜索
        if (name != null && !name.isEmpty()) {
            query.addCriteria(Criteria.where("name").regex(name, "i"));
        }

        // 状态筛选
        if (status != null) {
            query.addCriteria(Criteria.where("status").is(status));
        } else {
            query.addCriteria(Criteria.where("status").ne("Deleted"));
        }

        // 私有性筛选
        if (Private != null) {
            query.addCriteria(Criteria.where("Private").is(Private));

            if (Private) {
                query.addCriteria(Criteria.where("holder.$id").is(userId));
            }
        }

        // 可参与性筛选
        if (participatable != null && participatable) {
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("everyone").is(true),
                    Criteria.where("whiteList.$id").is(userId)
            ));
        }

        return mongoTemplate.find(query, Exam.class);
    }

    public List<Exam> adminSearchExams(String name, String status, Boolean isPrivate) {
        Query query = new Query();

        // 名称模糊搜索
        if (name != null && !name.isEmpty()) {
            query.addCriteria(Criteria.where("name").regex(name, "i"));
        }

        // 状态筛选
        if (status != null) {
            query.addCriteria(Criteria.where("status").is(status));
        } else {
            query.addCriteria(Criteria.where("status").ne("Deleted"));
        }

        // 私有性筛选
        if (isPrivate != null) {
            query.addCriteria(Criteria.where("isPrivate").is(isPrivate));
        }

        return mongoTemplate.find(query, Exam.class);
    }



    public void joinExam(String examId, String userId) {
        Optional<Exam> examOpt = examRepository.findById(new ObjectId(examId));
        Optional<User> userOpt = userRepository.findById(new ObjectId(userId));

        if (!examOpt.isPresent() || !userOpt.isPresent()) {
            throw new DefaultException("考试或用户不存在");
        }

        Exam exam = examOpt.get();
        User user = userOpt.get();

        // 更新参加者列表
        if (!exam.getParticipantList().contains(user)) {
            exam.getParticipantList().add(user);
            examRepository.save(exam);
        }

        // 创建考试记录
        ExamRecord record = new ExamRecord();
        record.setExam(exam);
        record.setUser(user);
        record.setStatus("未完成");
        record.setScore(0); // 初始化得分为0

        examRecordRepository.save(record);
    }

    public void addUserToWhitelist(String examId, String userId) {
        Optional<Exam> examOpt = examRepository.findById(new ObjectId(examId));
        Optional<User> userOpt = userRepository.findById(new ObjectId(userId));

        if (!examOpt.isPresent() || !userOpt.isPresent()) {
            throw new DefaultException("考试或用户不存在");
        }

        Exam exam = examOpt.get();
        User user = userOpt.get();

        if (!exam.getWhiteList().contains(user)) {
            exam.getWhiteList().add(user);
            examRepository.save(exam);
        }
    }

    public void removeUserFromWhitelist(String examId, String userId) {
        Optional<Exam> examOpt = examRepository.findById(new ObjectId(examId));
        Optional<User> userOpt = userRepository.findById(new ObjectId(userId));

        if (!examOpt.isPresent() || !userOpt.isPresent()) {
            throw new DefaultException("考试或用户不存在");
        }

        Exam exam = examOpt.get();
        User userToRemove = userOpt.get();

        if (exam.getWhiteList().contains(userToRemove)) {
            exam.getWhiteList().remove(userToRemove);
            examRepository.save(exam);
        }
    }



}
