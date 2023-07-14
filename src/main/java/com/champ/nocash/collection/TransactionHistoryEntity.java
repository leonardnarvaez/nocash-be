package com.champ.nocash.collection;

import com.champ.nocash.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Entity(name="transaction_history")
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String userId;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private String payee;
    private String referenceNumber;
    private String accountNumber;
    private LocalDateTime creationTime;
}
