package com.example.wechat.repository;

import com.example.wechat.model.Drug;
import com.example.wechat.model.Facility;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DrugRepository extends MongoRepository<Drug, ObjectId> {
    // 标准的CRUD操作已由MongoRepository提供

    Optional<Drug> findDrugByName(String name);

    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Drug> findDrugByNameLike(String regex);
}
