package com.champ.nocash.service.impl;

import com.champ.nocash.collection.TransactionHistoryEntity;
import com.champ.nocash.repository.TransactionHistoryEntityRepository;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.TransactionHistoryEntityService;
import com.champ.nocash.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class TransactionHistoryEntityServiceImpl implements TransactionHistoryEntityService {
    @Autowired
    private TransactionHistoryEntityRepository transactionHistoryEntityRepository;
    @Autowired
    private SecurityUtil securityUtil;

    @Override
    public TransactionHistoryEntity save(TransactionHistoryEntity transaction) throws Exception {
        transaction.setUserId(securityUtil.getUserEntity().getId());
        transaction.setReferenceNumber(UUIDUtil.generateUniqueIdAsString());
        transaction.setDate(LocalDateTime.now());
        return transactionHistoryEntityRepository.save(transaction);
    }
}
