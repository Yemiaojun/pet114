package com.example.wechat.repository;

import com.example.wechat.model.QuestionRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface QuestionRecordRepository extends MongoRepository<QuestionRecord, ObjectId> {
    // 标准的CRUD操作已由MongoRepository提供
    @Query("{'user.$id': ?0}")
    List<QuestionRecord> findByUserId(ObjectId userId);
}
