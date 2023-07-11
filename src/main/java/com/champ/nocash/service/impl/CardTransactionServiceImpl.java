package com.champ.nocash.service.impl;

import com.champ.nocash.collection.TransactionHistoryEntity;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.enums.TransactionType;
import com.champ.nocash.service.CardEntityService;
import com.champ.nocash.service.CardTransactionService;
import com.champ.nocash.service.TransactionHistoryEntityService;
import com.champ.nocash.service.WalletTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
public class CardTransactionServiceImpl implements CardTransactionService {

    @Autowired
    WalletTransactionService walletTransactionService;

    @Autowired
    TransactionHistoryEntityService transactionHistoryEntityService;

    @Autowired
    CardEntityService cardEntityService;

    @Override
    public boolean cashIn(BigDecimal amount, String cardId) {
       return walletTransactionService.deposit(amount, TransactionType.CASH_IN, cardEntityService.findCardById(cardId).getName(), "");
    }

    @Override
    public boolean cashOut(BigDecimal amount, String cardId) {
        return walletTransactionService.withdraw(amount, TransactionType.CASH_OUT, cardEntityService.findCardById(cardId).getName(), "");
    }
}
