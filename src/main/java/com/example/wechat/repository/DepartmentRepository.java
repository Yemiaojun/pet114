package com.example.wechat.repository;

import com.example.wechat.model.Department;
import com.example.wechat.model.Disease;
import com.example.wechat.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends MongoRepository<Department, ObjectId> {
    Optional<Department> findDepartmentById(ObjectId id);

    Optional<Department> findDepartmentByName(String name);

    List<Department> findAll();

    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Department> findDepartmentByNameLike(String regex);


}
