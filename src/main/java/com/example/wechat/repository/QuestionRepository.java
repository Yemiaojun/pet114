package com.example.wechat.repository;

import com.example.wechat.model.Category;
import com.example.wechat.model.Disease;
import com.example.wechat.model.Drug;
import com.example.wechat.model.Question;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface QuestionRepository extends MongoRepository<Question, ObjectId> {
    @Query("{ 'category.$id': ?0 }")
    List<Question> findByCategoryId(ObjectId categoryId);
    // 标准的CRUD操作已由MongoRepository提供
}
