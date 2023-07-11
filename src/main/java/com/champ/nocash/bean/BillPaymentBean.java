package com.champ.nocash.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BillPaymentBean {
    private String merchantId;
    private String accountNumber;
    private Double amount;
    private String pin;
}
