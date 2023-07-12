package com.champ.nocash.service;

import java.math.BigDecimal;

public interface BillPaymentService {
    boolean payBill(BigDecimal amount, String merchantId, String accountNumber, String pin) throws Exception;
}
