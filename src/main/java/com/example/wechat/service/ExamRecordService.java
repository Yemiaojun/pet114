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
public class ExamRecordService {

    @Autowired
    private ExamRecordRepository examRecordRepository;

    public List<ExamRecord> findExamRecordsByExamId(String examId, String sort) {
        ObjectId id = new ObjectId(examId);
        List<ExamRecord> records = examRecordRepository.findByExamId(id);

        // 如果sort参数为"score"，则按照score降序排序
        if ("score".equals(sort)) {
            records.sort(Comparator.comparingInt(ExamRecord::getScore).reversed());
        }

        return records;
    }
}
