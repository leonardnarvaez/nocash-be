package com.champ.nocash.service.impl;

import com.champ.nocash.collection.TransactionHistoryEntity;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.enums.TransactionType;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.TransactionHistoryEntityService;
import com.champ.nocash.service.UserEntityService;
import com.champ.nocash.service.WalletTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
public class WalletTransactionServiceImpl implements WalletTransactionService {
    @Autowired
    private UserEntityService userEntityService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private TransactionHistoryEntityService transactionHistoryEntityService;

    @Override
    public boolean deposit(BigDecimal amount, TransactionType transactionType, String payee) {
        UserEntity currentUser = securityUtil.getUserEntity();
        currentUser.getWallet().deposit(amount);
        try {
            userEntityService.updateUser(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        TransactionHistoryEntity newTransaction = TransactionHistoryEntity.builder()
                .amount(amount)
                .transactionType(transactionType)
                .payee(payee)
                .build();
        try {
            transactionHistoryEntityService.save(newTransaction);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean withdraw(BigDecimal amount, TransactionType transactionType, String payee) {
        UserEntity currentUser = securityUtil.getUserEntity();
        currentUser.getWallet().withdraw(amount);
        try {
            userEntityService.updateUser(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        TransactionHistoryEntity newTransaction = TransactionHistoryEntity.builder()
                .amount(amount)
                .transactionType(transactionType)
                .payee(payee)
                .build();
        try {
            transactionHistoryEntityService.save(newTransaction);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
