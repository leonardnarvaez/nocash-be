package com.champ.nocash.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionListBean {
    private LocalDate startDate;
    private LocalDate endDate;

    public boolean isValid() {
        return (startDate != null && endDate != null) && (startDate.isBefore(endDate));
    }
}
