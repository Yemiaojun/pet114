package com.example.wechat.repository;

import com.example.wechat.model.Drug;
import com.example.wechat.model.Exam;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExamRepository extends MongoRepository<Exam, ObjectId> {
    // 标准的CRUD操作已由MongoRepository提供
}
