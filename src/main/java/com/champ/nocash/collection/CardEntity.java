package com.champ.nocash.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Builder
public class CardEntity {
    @Id
    private String id;
    private String name;
    private String accountNumber;
    private String expiryDate;
    private String cvv;
    private LocalDateTime createdDate;
}
