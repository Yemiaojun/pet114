package com.example.wechat.repository;

import com.example.wechat.model.QuestionRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuestionRecordRepository extends MongoRepository<QuestionRecord, ObjectId> {
    // 标准的CRUD操作已由MongoRepository提供
}
