package com.example.wechat.repository;

import com.example.wechat.model.Assay;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AssayRepository extends MongoRepository<Assay, ObjectId> {
    Optional<Assay> findAssayById(ObjectId id);

    Optional<Assay> findAssayByName(String name);

    List<Assay> findAll();

    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Assay> findAssayByNameLike(String regex);
}
