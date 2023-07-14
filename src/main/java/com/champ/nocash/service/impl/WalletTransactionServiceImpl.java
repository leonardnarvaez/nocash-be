package com.champ.nocash.service.impl;

import com.champ.nocash.collection.TransactionHistoryEntity;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.entity.WalletEntity;
import com.champ.nocash.enums.TransactionType;
import com.champ.nocash.repository.WalletRepository;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.TransactionHistoryEntityService;
import com.champ.nocash.service.WalletTransactionService;
import com.champ.nocash.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

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

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void transfer(WalletEntity recipientWallet, BigDecimal amount, UserEntity recipient, UserEntity currentUser) throws Exception {
        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount should not be negative or zero");
        }
        if(recipient == null || currentUser == null) {
            throw new IllegalArgumentException("Recipient and Current user object must not be null");
        }
        if(recipientWallet == null) {
            throw new IllegalArgumentException("Recipient wallet must not be null");
        }
        if(currentUser.getMobileNumber().equals(recipient.getMobileNumber())) {
            throw new IllegalArgumentException("[self transfer]: Recipient and current user must not be the same");
        }
        String currentUserId = currentUser.getId();
        WalletEntity senderWallet = walletRepository.findByUserId(currentUserId);

        String recipientUserId = recipient.getId();

        // financial transactions
        senderWallet.withdraw(amount);
        recipientWallet.deposit(amount);

        // logging
        String referenceNumber = UUIDUtil.generateUniqueIdAsString();
        LocalDateTime transactionTime = LocalDateTime.now();
        // sender log
        TransactionHistoryEntity senderTransaction = TransactionHistoryEntity.builder()
                .userId(currentUserId)
                .amount(amount)
                .transactionType(TransactionType.TRANSFER_TO)
                .payee(recipient.getUsername())
                .referenceNumber(referenceNumber)
                .accountNumber(recipient.getMobileNumber())
                .creationTime(transactionTime)
                .build();

        // recipient log
        TransactionHistoryEntity recipientTransaction = TransactionHistoryEntity.builder()
                .userId(recipientUserId)
                .amount(amount)
                .transactionType(TransactionType.TRANSFER_FROM)
                .payee(currentUser.getUsername())
                .referenceNumber(referenceNumber)
                .accountNumber(currentUser.getMobileNumber())
                .creationTime(transactionTime)
                .build();

        // save the records
        walletRepository.save(senderWallet);
        walletRepository.save(recipientWallet);

        transactionHistoryEntityService.saveAsIs(senderTransaction);
        transactionHistoryEntityService.saveAsIs(recipientTransaction);

    }

    @Override
    public WalletEntity getWalletByUserId(String userId) {
        return walletRepository.findByUserId(userId);
    }

}
