package com.example.wechat.service;

import com.example.wechat.model.ExamRecord;
import com.example.wechat.repository.ExamRecordRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamRecordService {

    @Autowired
    private ExamRecordRepository examRecordRepository;

    public List<ExamRecord> findExamRecordsByExamId(String examId, String sort, String status) {
        ObjectId id = new ObjectId(examId);
        List<ExamRecord> records = examRecordRepository.findByExamId(id);

        // 筛选记录的状态，如果status参数不为null
        if (status != null && !status.isEmpty()) {
            records = records.stream().filter(r -> status.equals(r.getStatus())).collect(Collectors.toList());
        }

        // 排序逻辑保持不变
        if ("score".equals(sort)) {
            records.sort(Comparator.comparingInt(ExamRecord::getScore).reversed());
        }

        return records;
    }


    public List<ExamRecord> findExamRecordsByUserId(String userId, String sort, String status) {
        ObjectId id = new ObjectId(userId);
        List<ExamRecord> records = examRecordRepository.findByUserId(id);

        // 筛选记录的状态，如果status参数不为null
        if (status != null && !status.isEmpty()) {
            records = records.stream().filter(r -> status.equals(r.getStatus())).collect(Collectors.toList());
        }

        // 排序逻辑保持不变
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
