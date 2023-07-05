package com.champ.nocash.service;

import com.champ.nocash.collection.UserEntity;

public interface UserEntityService {
    UserEntity findUserByMobile(String mobileNumber);
    UserEntity findUserByEmail(String email);
    UserEntity save(UserEntity user) throws Exception;
    UserEntity updateUser(UserEntity user) throws Exception;
}
