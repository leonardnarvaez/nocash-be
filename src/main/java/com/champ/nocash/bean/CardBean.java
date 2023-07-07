package com.champ.nocash.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardBean {
    private String accountNumber;
    private String expiryDate;
    private String cvv;
    private String name;


    public boolean isAccountNumberValid() {
        if (this.accountNumber.length() == 12) {
            return true;
        }
        return false;
    }
    public boolean isCvvValid() {
        if (this.cvv.length() == 3) {
            return true;
        }
        return false;
    }
}
