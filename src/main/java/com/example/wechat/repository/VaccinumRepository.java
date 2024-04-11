package com.example.wechat.repository;

import com.example.wechat.model.Vaccinum;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VaccinumRepository extends MongoRepository<Vaccinum, ObjectId> {
    Optional<Vaccinum> findVaccinumById(ObjectId id);

    Optional<Vaccinum> findVaccinumByName(String name);

    List<Vaccinum> findAll();

    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Vaccinum> findVaccinumByNameLike(String regex);
}
