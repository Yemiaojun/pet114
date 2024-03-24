package com.example.wechat.repository;

import com.example.wechat.model.Facility;
import com.example.wechat.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FacilityRepository extends MongoRepository<Facility, ObjectId> {
    Optional<Facility> findFacilityByName(String name);


    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Facility> findFacilityByNameLike(String regex);
 }
