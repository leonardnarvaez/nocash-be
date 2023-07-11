package com.champ.nocash.service.impl;

import com.champ.nocash.collection.MerchantEntity;
import com.champ.nocash.enums.TransactionType;
import com.champ.nocash.service.BillPaymentService;
import com.champ.nocash.service.MerchantEntityService;
import com.champ.nocash.service.WalletTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BillPaymentServiceImpl implements BillPaymentService {

    @Autowired
    WalletTransactionService walletTransactionService;

    @Autowired
    MerchantEntityService merchantEntityService;
    @Override
    public boolean payBill(BigDecimal amount, String merchantId, String accountNumber) throws Exception {

        MerchantEntity retrievedMerchant = merchantEntityService.findByMerchantId(merchantId);
        if(retrievedMerchant == null) {
            throw new Exception("No merchant found");
        }
        return walletTransactionService.withdraw(amount, TransactionType.CASH_OUT, retrievedMerchant.getName(), accountNumber);
    }
}
