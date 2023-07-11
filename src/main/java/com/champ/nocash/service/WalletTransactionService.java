package com.champ.nocash.service;

import com.champ.nocash.collection.Wallet;
import com.champ.nocash.enums.TransactionType;

import java.math.BigDecimal;

public interface WalletTransactionService {
    boolean deposit(BigDecimal amount, TransactionType transactionType, String payee,String accountNumber);

    boolean withdraw(BigDecimal amount, TransactionType transactionType, String payee, String accountNumber) throws Exception;

    BigDecimal getBalance();
}
