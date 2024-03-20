package com.example.wechat.repository;

import com.example.wechat.model.Drug;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DrugRepository extends MongoRepository<Drug, ObjectId> {
    // 标准的CRUD操作已由MongoRepository提供
}
