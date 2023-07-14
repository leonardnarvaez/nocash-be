package com.champ.nocash.collection;

import com.champ.nocash.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "transaction")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionHistoryEntity {
    @Id
    private String id;
    private String userId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String payee;
    private String referenceNumber;
    private String accountNumber;
    private LocalDateTime date;

}
