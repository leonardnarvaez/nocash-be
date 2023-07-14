package com.champ.nocash.service;

import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.collection.Wallet;
import com.champ.nocash.entity.WalletEntity;
import com.champ.nocash.enums.TransactionType;

import java.math.BigDecimal;
import java.util.Optional;

public interface WalletTransactionService {
    boolean deposit(BigDecimal amount, TransactionType transactionType, String payee,String accountNumber);

    boolean withdraw(BigDecimal amount, TransactionType transactionType, String payee, String accountNumber) throws Exception;

    BigDecimal getBalance();
    WalletEntity save(WalletEntity wallet);
    void transfer(WalletEntity recipientWallet, BigDecimal amount, UserEntity recipient, UserEntity currentUser) throws Exception;

    WalletEntity getWalletByUserId(String userId);
}
