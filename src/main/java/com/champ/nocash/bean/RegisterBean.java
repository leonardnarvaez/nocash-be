package com.champ.nocash.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterBean {
    @NotNull
    private String emailAddress;
    @NotNull
    @Size(min = 11, max = 11)
    private String mobileNumber;
    @NotNull
    @Size(min = 6, max = 6)
    private String pin;

}
