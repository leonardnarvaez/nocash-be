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

import javax.websocket.server.PathParam;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
            @RequestParam(value = "start-date", required = false) String startDateString,
            @RequestParam(value = "end-date", required = false) String endDateString) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        try {
            startDate = LocalDateTime.parse(startDateString, formatter);
            endDate = LocalDateTime.parse(endDateString, formatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
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
        return ResponseEntity.ok(transactionHistoryEntityService.getAll(transactionListBean.getStartDate(), transactionListBean.getEndDate()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findOne(@PathVariable String id) {
        TransactionHistoryEntity transactionHistoryEntity = transactionHistoryEntityService.getTransactionHistory(id);
        if(transactionHistoryEntity != null) {
            return ResponseEntity.ok(transactionHistoryEntity);
        } else {
            return new ResponseEntity(ErrorResponse.builder()
                    .error("Bad Request")
                    .message("No Transaction found")
                    .status(401)
                    .path("/authentication/authenticate")
                    .build(), HttpStatus.BAD_REQUEST);
        }
    }
}
