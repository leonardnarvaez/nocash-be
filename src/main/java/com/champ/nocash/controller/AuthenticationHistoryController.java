package com.champ.nocash.controller;

import com.champ.nocash.bean.TransactionListBean;
import com.champ.nocash.response.ErrorResponse;
import com.champ.nocash.service.AuthenticationHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/authentication-history")
public class AuthenticationHistoryController {
    @Autowired
    private AuthenticationHistoryService authenticationHistoryService;
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
        return ResponseEntity.ok(authenticationHistoryService.findAllByDate(transactionListBean.getStartDate(), transactionListBean.getEndDate()));
    }
}
