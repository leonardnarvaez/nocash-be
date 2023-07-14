package com.champ.nocash.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Document(collection = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserEntity {
    @Id
    private String id;
    private String emailAddress;
    private String mobileNumber;
    private String username;
    private String pin;
    private Boolean isActive;
    private Boolean isLocked;
    private LocalDateTime lastDateModified;
    private LocalDateTime timestamp;
    private LocalDateTime lastLoginDate;
    private List<CardEntity> cards;
    private LoginCounter loginCounter;
    private Wallet wallet;
    private Verification verification;
    private Salt salt;
}
