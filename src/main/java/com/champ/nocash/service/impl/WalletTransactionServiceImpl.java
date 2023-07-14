package com.champ.nocash.service.impl;

import com.champ.nocash.collection.TransactionHistoryEntity;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.entity.WalletEntity;
import com.champ.nocash.enums.TransactionType;
import com.champ.nocash.repository.WalletRepository;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.TransactionHistoryEntityService;
import com.champ.nocash.service.UserEntityService;
import com.champ.nocash.service.WalletTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
@Service
public class WalletTransactionServiceImpl implements WalletTransactionService {
    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private TransactionHistoryEntityService transactionHistoryEntityService;
    @Autowired
    private WalletRepository walletRepository;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean deposit(BigDecimal amount, TransactionType transactionType, String payee, String accountNumber) {
        String userId = SecurityUtil.getUserId();
        WalletEntity wallet = walletRepository.findByUserId(userId);
        wallet.deposit(amount);
        try {
            save(wallet);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        TransactionHistoryEntity newTransaction = TransactionHistoryEntity.builder()
                .amount(amount)
                .transactionType(transactionType)
                .payee(payee)
                .accountNumber(accountNumber)
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
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean withdraw(BigDecimal amount, TransactionType transactionType, String payee, String accountNumber) throws Exception {
        String userId = SecurityUtil.getUserId();
        WalletEntity wallet = walletRepository.findByUserId(userId);
        wallet.withdraw(amount);
        save(wallet);
        TransactionHistoryEntity newTransaction = TransactionHistoryEntity.builder()
                .amount(amount)
                .transactionType(transactionType)
                .payee(payee)
                .accountNumber(accountNumber)
                .build();
        transactionHistoryEntityService.save(newTransaction);
        return true;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BigDecimal getBalance() {
        String userId = SecurityUtil.getUserId();
        WalletEntity wallet = walletRepository.findByUserId(userId);
        return wallet.getBalance();
    }

    @Override
    public WalletEntity save(WalletEntity wallet) {
        return walletRepository.save(wallet);
    }
}
