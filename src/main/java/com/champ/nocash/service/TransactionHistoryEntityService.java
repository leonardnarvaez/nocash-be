package com.champ.nocash.service;

import com.champ.nocash.collection.TransactionHistoryEntity;

public interface TransactionHistoryEntityService {
    TransactionHistoryEntity save(TransactionHistoryEntity transaction) throws Exception;
}
