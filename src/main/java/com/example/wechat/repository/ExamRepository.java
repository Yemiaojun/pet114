package com.example.wechat.repository;

import com.example.wechat.model.Drug;
import com.example.wechat.model.Exam;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ExamRepository extends MongoRepository<Exam, ObjectId> {
    // 标准的CRUD操作已由MongoRepository提供

    List<Exam> findByStatus(String status);

    @Query("{'status': {$ne: ?0}}")
    List<Exam> findByStatusNot(String status);

    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Exam> findByNameLike(String name);

    // 复杂查询，考虑状态、私有性和可参与性
    @Query(value = "{'name': {$regex: ?0, $options: 'i'}, 'status': {$ne: 'Deleted'}, $or: [ {'Private': false}, {'holder.$id': ?1}, {'everyone': true}, {'whiteList.$id': ?1} ]}", exists = true)
    List<Exam> searchExamsWithCriteria(String name, ObjectId userId, String status, Boolean Private, Boolean everyone);
}

