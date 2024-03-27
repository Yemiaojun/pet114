package com.example.wechat.service;

import com.example.wechat.model.QuestionRecord;
import com.example.wechat.repository.QuestionRecordRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionRecordService {

    @Autowired
    private QuestionRecordRepository questionRecordRepository;

    public List<QuestionRecord> findQuestionRecordsByUserId(String userId) {
        return questionRecordRepository.findByUserId(new ObjectId(userId));
    }

    public List<QuestionRecord> findAllQuestionRecords() {
        return questionRecordRepository.findAll();
    }


}
