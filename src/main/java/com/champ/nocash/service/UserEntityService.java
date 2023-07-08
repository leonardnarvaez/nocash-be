package com.champ.nocash.service;

import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.request.AuthenticationRequest;
import com.champ.nocash.response.AuthenticationResponse;

import java.util.Optional;

public interface UserEntityService {
    UserEntity findUserByMobile(String mobileNumber);
    UserEntity findUserByEmail(String email);
    UserEntity save(UserEntity user) throws Exception;
    UserEntity updateUser(UserEntity user) throws Exception;

    AuthenticationResponse login(AuthenticationRequest authenticationRequest, String ipAddress, String userAgent) throws Exception;
    Optional<UserEntity> findUserById(String userId);
    void updatePIN(String oldPIN, String newPIN) throws Exception;
}
