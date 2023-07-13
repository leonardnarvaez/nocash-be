package com.champ.nocash.service.impl;

import com.champ.nocash.collection.TransactionHistoryEntity;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.enums.TransactionType;
import com.champ.nocash.service.*;
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

    @Autowired
    UserEntityService userEntityService;

    @Override
    public boolean cashIn(BigDecimal amount, String cardId, String pin) throws Exception {
        if (userEntityService.validatePIN(pin)) {
            return walletTransactionService.deposit(amount, TransactionType.CASH_IN, cardEntityService.findCardById(cardId).getName(), "");
        }
        throw new Exception("Invalid PIN");
    }

    @Override
    public boolean cashOut(BigDecimal amount, String cardId, String pin) throws Exception {
        if (userEntityService.validatePIN(pin)) {
            return walletTransactionService.withdraw(amount, TransactionType.CASH_OUT, cardEntityService.findCardById(cardId).getName(), "");
        }
        throw new Exception("Invalid PIN");
    }
}
