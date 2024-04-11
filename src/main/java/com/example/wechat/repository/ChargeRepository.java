package com.example.wechat.repository;

import com.example.wechat.model.Charge;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChargeRepository extends MongoRepository<Charge, ObjectId> {
    Optional<Charge> findChargeById(ObjectId id);

    Optional<Charge> findChargeByName(String name);

    List<Charge> findAll();

    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Charge> findChargeByNameLike(String regex);
}
