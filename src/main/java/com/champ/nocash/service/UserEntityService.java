package com.champ.nocash.service;

import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.request.AuthenticationRequest;
import com.champ.nocash.response.AuthenticationResponse;

public interface UserEntityService {
    UserEntity findUserByMobile(String mobileNumber);
    UserEntity findUserByEmail(String email);
    UserEntity save(UserEntity user) throws Exception;
    UserEntity updateUser(UserEntity user) throws Exception;

    AuthenticationResponse login(AuthenticationRequest authenticationRequest) throws Exception;
}
