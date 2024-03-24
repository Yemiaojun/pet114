package com.example.wechat.repository;

import com.example.wechat.model.Procedure;
import com.example.wechat.model.Question;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProcedureRepository extends MongoRepository<Procedure, ObjectId> {

    @Query("{ 'facility.$id': ?0 }")
    List<Procedure> findByFacilityId(ObjectId facilityId);
}
