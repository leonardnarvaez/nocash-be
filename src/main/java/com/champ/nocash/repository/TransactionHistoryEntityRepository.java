package com.champ.nocash.repository;

import com.champ.nocash.collection.TransactionHistoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionHistoryEntityRepository extends MongoRepository<TransactionHistoryEntity, String> {
    List<TransactionHistoryEntity> findByDateBetweenAndUserId(LocalDateTime startDate, LocalDateTime endDate, String userId);
}
