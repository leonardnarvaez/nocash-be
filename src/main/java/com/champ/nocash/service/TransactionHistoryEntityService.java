package com.champ.nocash.service;

import com.champ.nocash.collection.TransactionHistoryEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionHistoryEntityService {
    TransactionHistoryEntity save(TransactionHistoryEntity transaction) throws Exception;
    TransactionHistoryEntity saveAsIs(TransactionHistoryEntity trasaction) throws Exception;

    List<TransactionHistoryEntity> getAll(LocalDateTime startDate, LocalDateTime endDate);
    TransactionHistoryEntity getTransactionHistory(Long id);
}
