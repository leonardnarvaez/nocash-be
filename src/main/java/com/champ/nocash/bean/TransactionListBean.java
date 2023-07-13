package com.champ.nocash.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionListBean {
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public boolean isValid() {
        return (startDate != null && endDate != null) && (startDate.isBefore(endDate));
    }
}
