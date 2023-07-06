package com.champ.nocash.service;

import java.math.BigDecimal;

public interface CardTransactionService {
    boolean cashIn(BigDecimal amount, String cardId);

    boolean cashOut(BigDecimal amount, String cardId);
}
