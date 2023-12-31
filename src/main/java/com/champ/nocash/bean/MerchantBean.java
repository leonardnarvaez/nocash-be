package com.champ.nocash.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MerchantBean {
    private String merchantId;
    private String imagePath;
    private String name;
}
