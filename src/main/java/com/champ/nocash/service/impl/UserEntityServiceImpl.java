package com.champ.nocash.service.impl;

import com.champ.nocash.collection.AuthenticationHistoryEntity;
import com.champ.nocash.collection.LoginCounter;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.enums.AuthenticationType;
import com.champ.nocash.repository.UserEntityRepository;
import com.champ.nocash.request.AuthenticationRequest;
import com.champ.nocash.response.AuthenticationResponse;
import com.champ.nocash.response.ErrorResponse;
import com.champ.nocash.security.CustomUserDetailService;
import com.champ.nocash.service.AuthenticationHistoryService;
import com.champ.nocash.service.UserEntityService;
import com.champ.nocash.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class UserEntityServiceImpl implements UserEntityService {
    @Autowired
    private UserEntityRepository userEntityRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailService customUserDetailService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationHistoryService authenticationHistoryService;
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
        user.setCards(new ArrayList<>());
        user.setLoginCounter(new LoginCounter());
        return userEntityRepository.save(user);
    }

    @Override
    public UserEntity updateUser(UserEntity user) throws Exception {
        return userEntityRepository.save(user);
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) throws Exception {
        UserEntity userEntity = findUserByMobile(authenticationRequest.getMobileNumber());
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getMobileNumber(), authenticationRequest.getPin()));
        } catch (BadCredentialsException e) {
            if(userEntity != null) {
                // the user exists but the provided pin is incorrect
                AuthenticationHistoryEntity authenticationHistoryEntity = AuthenticationHistoryEntity.builder()
                        .userId(userEntity.getId())
                        .isAuthenticationResultSuccess(false)
                        .authenticationType(AuthenticationType.LOGIN)
                        .build();
                authenticationHistoryService.save(authenticationHistoryEntity);
                userEntity.getLoginCounter().increment();
                if(!userEntity.getLoginCounter().isValid()) {
                    userEntity.setIsLocked(true);
                }
                updateUser(userEntity);
            }
            throw new BadCredentialsException("hindi legit si boss");
        }
        if(userEntity != null) {
            // the user exists and the pin is correct
            AuthenticationHistoryEntity authenticationHistoryEntity = AuthenticationHistoryEntity.builder()
                    .userId(userEntity.getId())
                    .isAuthenticationResultSuccess(true)
                    .authenticationType(AuthenticationType.LOGIN)
                    .build();
            authenticationHistoryService.save(authenticationHistoryEntity);
        }
        if(userEntity.getIsLocked()) {
            throw new BadCredentialsException("Your account is now locked");
        }
        // update the last login time of user
        userEntity.setLastLoginDate(LocalDateTime.now());
        userEntity.getLoginCounter().reset();
        updateUser(userEntity);
        final UserDetails userDetails = customUserDetailService.loadUserByUsername(authenticationRequest.getMobileNumber());
        final String jwt = jwtUtil.generateToken(userDetails);
        return AuthenticationResponse.builder()
                .firstName("Jon")
                .lastName("Narva")
                .emailAddress(userEntity.getEmailAddress())
                .mobileNumber(userEntity.getMobileNumber())
                .jwt(jwt)
                .userID(userEntity.getId())
                .build();
    }
}
