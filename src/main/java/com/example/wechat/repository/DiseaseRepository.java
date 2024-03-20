package com.example.wechat.repository;

import com.example.wechat.model.Category;
import com.example.wechat.model.Disease;
import com.example.wechat.model.Drug;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DiseaseRepository extends MongoRepository<Disease, ObjectId> {
    Optional<Disease> findDiseaseById(ObjectId id);
    Optional<Disease> findDiseaseByName(String name);
    Optional<Disease[]> findDiseaseByNameAndCategory(String name, Category category);
}
