package com.champ.nocash.bean;

import com.champ.nocash.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistoryBean {
    private Float amount;
    private String transactionType;
    private String payee;

}
