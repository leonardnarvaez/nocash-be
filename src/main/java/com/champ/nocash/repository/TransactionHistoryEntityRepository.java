package com.champ.nocash.repository;

import com.champ.nocash.collection.TransactionHistoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionHistoryEntityRepository extends MongoRepository<TransactionHistoryEntity, String> {
}
