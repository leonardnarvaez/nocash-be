package com.champ.nocash.service;

import java.math.BigDecimal;

public interface CardTransactionService {
    boolean cashIn(BigDecimal amount, String cardId, String pin) throws Exception;

    boolean cashOut(BigDecimal amount, String cardId, String pin) throws Exception;
}
