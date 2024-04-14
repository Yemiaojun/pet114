package com.example.wechat.service;

import com.example.wechat.model.ExamRecord;
import com.example.wechat.repository.ExamRecordRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<ExamRecord> findExamRecordsByUserId(String userId, String sort) {
        ObjectId id = new ObjectId(userId);
        List<ExamRecord> records = examRecordRepository.findByUserId(id);

        // 如果sort参数为"score"，则按照score降序排序
        if ("score".equals(sort)) {
            records.sort(Comparator.comparingInt(ExamRecord::getScore).reversed());
        }

        return records;
    }
    public Optional<ExamRecord> findExamRecordsById(String Id) {
        ObjectId id = new ObjectId(Id);
        Optional<ExamRecord> record = examRecordRepository.findById(id);

        return record;
    }
}
