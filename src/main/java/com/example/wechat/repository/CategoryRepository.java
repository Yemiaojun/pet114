package com.example.wechat.repository;

import com.example.wechat.model.Category;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, ObjectId> {

    // 根据类别名称查找类别
    Optional<Category> findByName(String name);

    // 根据类别ID查找类别
    Optional<Category> findById(ObjectId id);

}
