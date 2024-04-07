package com.example.wechat.repository;

import com.example.wechat.model.Case;
import com.example.wechat.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CaseRepository extends MongoRepository<Case, ObjectId> {
    // 标准的CRUD操作已由MongoRepository提供

    // 为新的服务方法添加定义
    // 注意：对于修改操作，通常在Service层实现，因为直接的修改操作并不是Repository层的职责

    Optional<Case> findById(ObjectId id);
    Optional<Case> findByName(String name);

    //查找所有病例
    List<Case> findAll();



    //根据病例的描述模糊查找病例
    @Query("{'textList': {$regex: ?0, $options: 'i'}}")
    List<Case> findByTextListLike(String regex);


    //根据疾病的id查询病例
    @Query("{ 'disease.$id': ?0 }")
    List<Case> findByDiseaseId(ObjectId diseaseId);
}
