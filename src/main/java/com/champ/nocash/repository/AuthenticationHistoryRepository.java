package com.champ.nocash.repository;

import com.champ.nocash.collection.AuthenticationHistoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationHistoryRepository extends MongoRepository<AuthenticationHistoryEntity, String> {
}
