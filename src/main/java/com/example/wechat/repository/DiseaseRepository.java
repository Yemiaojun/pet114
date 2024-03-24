package com.example.wechat.repository;

import com.example.wechat.model.Category;
import com.example.wechat.model.Department;
import com.example.wechat.model.Disease;
import com.example.wechat.model.Drug;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DiseaseRepository extends MongoRepository<Disease, ObjectId> {
    Optional<Disease> findDiseaseById(ObjectId id);
    Optional<Disease> findDiseaseByName(String name);
    Optional<Disease[]> findDiseaseByNameAndCategory(String name, Category category);

    @Query("{ 'category.$id': ?0 }")
    List<Disease> findByCategoryId(ObjectId categoryId);

    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Disease> findDiseasesByNameLike(String regex);


}
