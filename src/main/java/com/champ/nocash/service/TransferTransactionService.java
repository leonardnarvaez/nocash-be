package com.champ.nocash.service;

import java.math.BigDecimal;

public interface TransferTransactionService {
    void transfer(String otherMobile, BigDecimal amount, String pin) throws Exception;
}
