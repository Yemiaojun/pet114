package com.example.wechat.repository;

import com.example.wechat.model.Exam;
import com.example.wechat.model.ExamRecord;
import com.example.wechat.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;

public interface ExamRecordRepository extends MongoRepository<ExamRecord, ObjectId> {

    // 使用@Query注解来自定义查询，这里假设ExamRecord中存储的是User和Exam的引用（DBRef）
    @Query("{'user.$id': ?0, 'exam.$id': ?1}")
    Optional<ExamRecord> findByUserAndExam(ObjectId userId, ObjectId examId);

    // 或者如果你直接在ExamRecord中存储User和Exam的ID，可以更简单地定义方法
    // Optional<ExamRecord> findByUserIdAndExamId(String userId, String examId);
}
