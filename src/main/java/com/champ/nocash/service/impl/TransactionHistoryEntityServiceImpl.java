package com.champ.nocash.service.impl;

import com.champ.nocash.collection.TransactionHistoryEntity;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.repository.TransactionHistoryEntityRepository;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.TransactionHistoryEntityService;
import com.champ.nocash.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        transaction.setCreationTime(LocalDateTime.now());
        return transactionHistoryEntityRepository.save(transaction);
    }

    @Override
    public List<TransactionHistoryEntity> getAll(LocalDateTime startDate, LocalDateTime endDate) {
        String userId = securityUtil.getUserId();
        return transactionHistoryEntityRepository.findByCreationTimeBetweenAndUserId(startDate, endDate, userId);
    }

    @Override
    public TransactionHistoryEntity getTransactionHistory(Long id) {
        return transactionHistoryEntityRepository.findByIdAndUserId(id, SecurityUtil.getUserId());
    }

}
