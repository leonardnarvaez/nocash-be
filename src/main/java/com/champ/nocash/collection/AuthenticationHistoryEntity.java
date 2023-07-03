package com.champ.nocash.collection;

import com.champ.nocash.enums.AuthenticationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "authentication_history")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationHistoryEntity {
    @Id
    private String id;
    private String userId;
    private AuthenticationType authenticationType;
    private String ipAddress;
    private String userAgent;
    private Boolean authenticationResult;
}
