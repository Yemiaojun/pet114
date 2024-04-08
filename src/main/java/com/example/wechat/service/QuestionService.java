package com.example.wechat.service;

import com.example.wechat.exception.DefaultException;
import com.example.wechat.model.*;
import com.example.wechat.repository.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @Autowired
    private QuestionRecordRepository questionRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamRecordRepository examRecordRepository;

    public Question addQuestion(Question question) {
        question.setVisible(true);
        return questionRepository.save(question);
    }

    public Optional<Question> updateQuestion(Question question) {
        Optional<Question> existingQuestion = questionRepository.findById(question.getId());
        if(existingQuestion.isPresent()) {
            // 这里可以添加其他的业务逻辑，例如检查问题内容的唯一性等
            // 更新问题信息
            return Optional.of(questionRepository.save(question));
        } else {
            return Optional.empty(); // 如果问题不存在，返回空Optional
        }
    }

    public Optional<Question> hideQuestion(String questionId) {
        Optional<Question> questionOptional = questionRepository.findById(new ObjectId(questionId));
        if(questionOptional.isPresent()) {
            Question question = questionOptional.get();
            question.setVisible(false); // 将问题设置为不可见
            questionRepository.save(question);
            return Optional.of(question);
        }
        return Optional.empty(); // 如果问题不存在，返回空Optional
    }


    public List<Question> findAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> findAllVisibleQuestions() {
        return questionRepository.findAllVisibleQuestions();
    }


    public List<Question> findQuestionsByCategoryId(ObjectId categoryId) {
        return questionRepository.findByCategoryId(categoryId);
    }

    public List<Question> findQuestionsByCategoryName(String categoryName) {
        Optional<Category> categoryOpt = categoryRepository.findByName(categoryName);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            return questionRepository.findByCategoryId(category.getId());
        }
        return List.of(); // 如果没有找到对应的Category，返回空列表
    }

    public Optional<Question> findQuestionById(String questionId) {
        return questionRepository.findById(new ObjectId(questionId));
    }

    public List<Question> findQuestionsByStemLike(String stem) {
        String regex = ".*" + stem + ".*";
        return questionRepository.findByStemLike(regex);
    }

    public List<Question> findVisibleQuestionsByCategoryId(ObjectId categoryId) {
        return questionRepository.findByCategoryIdAndVisible(categoryId);
    }

    public List<Question> findVisibleQuestionsByCategoryName(String categoryName) {
        Optional<Category> categoryOpt = categoryRepository.findByName(categoryName);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            return questionRepository.findByCategoryIdAndVisible(category.getId());
        }
        return List.of(); // 如果没有找到对应的Category，返回空列表
    }

    public List<Question> findVisibleQuestionsByStemLike(String stem) {
        String regex = ".*" + stem + ".*";
        return questionRepository.findByStemLikeAndVisible(regex);
    }

    public List<Question> getRandomQuestions(Integer n, String categoryIdStr) {
        List<Question> questions;
        if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            ObjectId categoryId = new ObjectId(categoryIdStr);
            questions = findVisibleQuestionsByCategoryId(categoryId);
        } else {
            questions = findAllVisibleQuestions();
        }
        if(n<=0){
            throw new DefaultException("非法的参数");
        }
        // 打乱问题列表并取前n个
        Collections.shuffle(questions);
        return questions.stream().limit(n).collect(Collectors.toList());
    }


    public void checkQuestionAnswers(List<String> answerList, List<Question> questionList, String examId, String userId) {
        Optional<User> userOpt = userRepository.findById(new ObjectId(userId));
        Optional<Exam> examOpt = examRepository.findById(new ObjectId(examId));

        if (!userOpt.isPresent() || !examOpt.isPresent()) {
            // 这里可以抛出一个自定义的异常或者处理用户和考试不存在的情况
            throw new DefaultException("考试不存在");
        }

        User user = userOpt.get();
        Exam exam = examOpt.get();

        // 尝试找到现有的ExamRecord

        Optional<ExamRecord> existingRecordOpt = examRecordRepository.findByUserAndExam(user.getId(), exam.getId());

        ExamRecord examRecord;
        if (existingRecordOpt.isPresent()) {
            examRecord = existingRecordOpt.get();
        } else {
            examRecord = new ExamRecord();
            examRecord.setExam(exam);
            examRecord.setUser(user);
            examRecord.setScore(0); // 初始化得分为0
        }

        double totalScoreEarned = 0;
        int totalExamScore = exam.getScore();

        for (int i = 0; i < questionList.size(); i++) {
            Question question = questionList.get(i);
            String providedAnswer = answerList.get(i);

            QuestionRecord record = new QuestionRecord();
            record.setQuestion(question);
            record.setChoice(providedAnswer);
            record.setTorF(question.getAnswer().equals(providedAnswer));
            record.setUser(user); // 使用查询得到的User实例
            record.setExam(exam); // 使用查询得到的Exam实例
            record.setTime(new Date());
            questionRecordRepository.save(record);

            // 更新得分
            if (record.getTorF()) {
                double questionScoreRatio = (double)question.getScore() / totalExamScore;
                double scoreForThisQuestion = questionScoreRatio * exam.getScore();
                totalScoreEarned += scoreForThisQuestion;
            }
        }

        // 更新或设置ExamRecord的得分
        examRecord.setScore((int)Math.round(totalScoreEarned));
        examRecord.setStatus("已完成");
        examRecordRepository.save(examRecord);
    }



}
