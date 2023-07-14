package com.champ.nocash.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoneyTransferBean {
    private String mobileNumber;
    private Double amount;
    private String pin;
}
