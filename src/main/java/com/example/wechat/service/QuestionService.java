package com.example.wechat.service;

import com.example.wechat.model.Category;
import com.example.wechat.model.Question;
import com.example.wechat.repository.CategoryRepository;
import com.example.wechat.repository.QuestionRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

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
}
