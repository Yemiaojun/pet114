package com.example.wechat.repository;

import com.example.wechat.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    // 已经定义的方法
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndPassword(String username, String password);

    // 为新的服务方法添加定义
    // 注意：对于修改操作，通常在Service层实现，因为直接的修改操作并不是Repository层的职责

    @Query("{'username': {$regex: ?0, $options: 'i'}}")
    List<User> findByUsernameLike(String regex);
}
