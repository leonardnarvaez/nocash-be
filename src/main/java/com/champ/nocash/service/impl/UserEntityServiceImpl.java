package com.champ.nocash.service.impl;

import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.repository.UserEntityRepository;
import com.champ.nocash.service.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class UserEntityServiceImpl implements UserEntityService {
    @Autowired
    private UserEntityRepository userEntityRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public UserEntity findUserByMobile(String mobileNumber) {
        return userEntityRepository.findFirstByMobileNumber(mobileNumber);
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userEntityRepository.findFirstByEmailAddress(email);
    }

    @Override
    public UserEntity save(UserEntity user) throws Exception {
        UserEntity existingUser = findUserByEmail(user.getEmailAddress());
        if(existingUser != null) {
            throw new Exception("Email already exists");
        }
        existingUser = findUserByMobile(user.getMobileNumber());
        if(existingUser != null) {
            throw new Exception("Mobile number already exists");
        }
        user.setPin(passwordEncoder.encode(user.getPin()));
        user.setIsLocked(false);
        user.setIsActive(true);
        user.setTimestamp(LocalDateTime.now());
        return userEntityRepository.save(user);
    }
}
