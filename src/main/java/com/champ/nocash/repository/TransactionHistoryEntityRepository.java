package com.champ.nocash.repository;

import com.champ.nocash.collection.TransactionHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionHistoryEntityRepository extends JpaRepository<TransactionHistoryEntity, Long> {
    List<TransactionHistoryEntity> findByCreationTimeBetweenAndUserId(LocalDateTime startDate, LocalDateTime endDate, String userId);
    TransactionHistoryEntity findByIdAndUserId(Long id, String userId);
}
