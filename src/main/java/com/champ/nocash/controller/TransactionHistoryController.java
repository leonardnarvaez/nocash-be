package com.champ.nocash.controller;

import com.champ.nocash.bean.TransactionHistoryBean;
import com.champ.nocash.bean.TransactionListBean;
import com.champ.nocash.collection.TransactionHistoryEntity;
import com.champ.nocash.enums.TransactionType;
import com.champ.nocash.response.ErrorResponse;
import com.champ.nocash.service.TransactionHistoryEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/transaction")
public class TransactionHistoryController {
    @Autowired
    private TransactionHistoryEntityService transactionHistoryEntityService;
    @PostMapping("/create")
    public ResponseEntity<?> save(@RequestBody TransactionHistoryBean transactionHistoryBean) throws Exception {
        TransactionType type;
        if(transactionHistoryBean.getTransactionType().equals("CASH_OUT")) {
            type = TransactionType.CASH_OUT;
        } else {
            type = TransactionType.CASH_IN;
        }
        TransactionHistoryEntity transactionHistory = TransactionHistoryEntity.builder()
                .amount(BigDecimal.valueOf(transactionHistoryBean.getAmount()))
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

    @GetMapping("/")
    public ResponseEntity<?> getAll(
            @RequestParam(value = "start-date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "end-date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        TransactionListBean transactionListBean = TransactionListBean.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
        if(!transactionListBean.isValid()) {
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Bad Request")
                    .message("Invalid date format")
                    .status(401)
                    .path("/authentication/authenticate")
                    .build(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(transactionHistoryEntityService.getAll(transactionListBean.getStartDate().atStartOfDay(), transactionListBean.getEndDate().atStartOfDay().plusHours(24)));
    }
}
