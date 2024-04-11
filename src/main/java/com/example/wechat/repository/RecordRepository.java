package com.example.wechat.repository;

import com.example.wechat.model.Record;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RecordRepository extends MongoRepository<Record, ObjectId> {
    Optional<Record> findRecordById(ObjectId id);

    Optional<Record> findRecordByName(String name);

    List<Record> findAll();

    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Record> findRecordByNameLike(String regex);
}
