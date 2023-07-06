package com.champ.nocash.repository;

import com.champ.nocash.collection.AuthenticationHistoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthenticationHistoryRepository extends MongoRepository<AuthenticationHistoryEntity, String> {
    List<AuthenticationHistoryEntity> findByUserId(String userId);
}
