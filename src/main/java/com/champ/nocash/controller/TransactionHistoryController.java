package com.champ.nocash.controller;

import com.champ.nocash.bean.TransactionHistoryBean;
import com.champ.nocash.collection.TransactionHistoryEntity;
import com.champ.nocash.enums.TransactionType;
import com.champ.nocash.response.ErrorResponse;
import com.champ.nocash.service.TransactionHistoryEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TransactionHistoryController {
    @Autowired
    private TransactionHistoryEntityService transactionHistoryEntityService;
    @PostMapping("/transaction")
    public ResponseEntity<?> save(@RequestBody TransactionHistoryBean transactionHistoryBean) throws Exception {
        TransactionType type;
        if(transactionHistoryBean.getTransactionType().equals("CREDIT")) {
            type = TransactionType.CREDIT;
        } else {
            type = TransactionType.DEBIT;
        }
        TransactionHistoryEntity transactionHistory = TransactionHistoryEntity.builder()
                .amount(transactionHistoryBean.getAmount())
                .transactionType(type)
                .payee(transactionHistoryBean.getPayee())
                .build();
        TransactionHistoryEntity newTransactionHistory = null;
        try {
            newTransactionHistory = transactionHistoryEntityService.save(transactionHistory);
        } catch(Exception e) {
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Bad Request")
                    .message(e.getMessage())
                    .status(401)
                    .path("/authentication/authenticate")
                    .build(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(newTransactionHistory);
    }
}
