package com.example.wechat.repository;

import com.example.wechat.model.Activity;
import com.example.wechat.model.Inpatient;
import com.example.wechat.model.Procedure;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends MongoRepository<Activity, ObjectId> {
    @Query("{ 'role.$id': ?0 }")
    List<Activity> findByRoleId(ObjectId roleId);

    Optional<Activity> findActivityByName(String name);
}
