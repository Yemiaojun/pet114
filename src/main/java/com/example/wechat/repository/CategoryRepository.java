package com.example.wechat.repository;

import com.example.wechat.model.Category;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, ObjectId> {

    // 根据类别名称查找类别
    Optional<Category> findByName(String name);

    // 根据类别ID查找类别
    Optional<Category> findById(ObjectId id);

    // 查找所有类别
    List<Category> findAll();

    // 根据类别名称模糊查找类别
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Category> findByNameLike(String name);




}
