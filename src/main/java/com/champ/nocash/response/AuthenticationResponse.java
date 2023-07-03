package com.champ.nocash.response;

import lombok.*;

@Builder
@Data
public class AuthenticationResponse {
    private final String jwt;
    private final String mobileNumber;
    private final String emailAddress;
    private final String firstName;
    private final String lastName;
    private final String userID;

    public AuthenticationResponse(String jwt, String mobileNumber, String emailAddress, String firstName, String lastName, String userID) {
        this.jwt = jwt;
        this.mobileNumber = mobileNumber;
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
    }

//    public AuthenticationResponse(String jwt) {
//        this.jwt = jwt;
//    }

    public String getJwt() {
        return jwt;
    }
}
