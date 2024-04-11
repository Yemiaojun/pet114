package com.example.wechat.repository;

import com.example.wechat.model.Assay;
import com.example.wechat.model.Inpatient;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InpatientRepository extends MongoRepository<Inpatient, ObjectId> {
    Optional<Inpatient> findInpatientById(ObjectId id);

    Optional<Inpatient> findInpatientByName(String name);

    List<Inpatient> findAll();

    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Inpatient> findInpatientByNameLike(String regex);
}
