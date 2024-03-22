package com.example.wechat.service;

import com.example.wechat.model.Question;
import com.example.wechat.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public Question addQuestion(Question question) {
        question.setVisible(true);
        return questionRepository.save(question);
    }
}
