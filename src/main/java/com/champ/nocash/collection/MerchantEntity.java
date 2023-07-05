package com.champ.nocash.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "merchant")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantEntity {
    @Id
    private String id;
    private String merchantId;
    private String name;
    private String imagePath;
    private LocalDateTime createdAt;
}
