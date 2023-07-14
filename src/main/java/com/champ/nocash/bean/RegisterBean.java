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

    private String emailAddress;

    private String mobileNumber;

    private String username;

    private String pin;

    public boolean isValid() {
        if (this.pin.length() == 4) {
            return true;
        }
        return false;
    }

}
