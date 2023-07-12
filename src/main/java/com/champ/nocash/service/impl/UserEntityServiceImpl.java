package com.champ.nocash.service.impl;

import com.champ.nocash.collection.*;
import com.champ.nocash.enums.AuthenticationType;
import com.champ.nocash.repository.UserEntityRepository;
import com.champ.nocash.request.AuthenticationRequest;
import com.champ.nocash.response.AuthenticationResponse;
import com.champ.nocash.response.ErrorResponse;
import com.champ.nocash.security.CustomUserDetailService;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.AuthenticationHistoryService;
import com.champ.nocash.service.UserEntityService;
import com.champ.nocash.util.EmailMessageProvider;
import com.champ.nocash.util.EmailService;
import com.champ.nocash.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

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
    @Autowired
    private EmailService emailService;
    @Autowired
    private SecurityUtil securityUtil;
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
        user.setIsActive(false);
        user.setTimestamp(LocalDateTime.now());
        user.setCards(new ArrayList<>());
        user.setLoginCounter(new LoginCounter());
        user.setWallet(new Wallet());
        user.setVerification(Verification.generateAccountReactivation());
        user.setSalt(new Salt());
        return userEntityRepository.save(user);
    }

    @Override
    public UserEntity updateUser(UserEntity user) throws Exception {
        return userEntityRepository.save(user);
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest, String ipAddress, String userAgent) throws Exception {
        UserEntity userEntity = findUserByMobile(authenticationRequest.getMobileNumber());
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getMobileNumber(), authenticationRequest.getPin()));
        } catch (BadCredentialsException e) {
            if(userEntity != null) {
                // the user exists but the provided pin is incorrect
                AuthenticationHistoryEntity authenticationHistoryEntity = AuthenticationHistoryEntity.builder()
                        .userId(userEntity.getId())
                        .userAgent(userAgent)
                        .ipAddress(ipAddress)
                        .isAuthenticationResultSuccess(false)
                        .authenticationType(AuthenticationType.LOGIN)
                        .build();
                authenticationHistoryService.save(authenticationHistoryEntity);
                userEntity.getLoginCounter().increment();
                if(!userEntity.getLoginCounter().isValid() && !userEntity.getIsLocked()) {
                    userEntity.setIsLocked(true);
                    AuthenticationHistoryEntity lock = AuthenticationHistoryEntity.builder()
                            .userId(userEntity.getId())
                            .userAgent(userAgent)
                            .ipAddress(ipAddress)
                            .isAuthenticationResultSuccess(true)
                            .authenticationType(AuthenticationType.ACCOUNT_LOCK)
                            .build();
                    authenticationHistoryService.save(lock);
                    emailService.sendMIMEMessage(
                            userEntity.getEmailAddress(),
                            "Account Locked",
                            EmailMessageProvider.getAccountLockMessage("Jon narva", ipAddress, userAgent, LocalDateTime.now()));
                }
                updateUser(userEntity);
                if(userEntity.getIsLocked()) {
                    throw new BadCredentialsException("Your account has been locked");
                }
            }
            throw new BadCredentialsException("hindi legit si boss");
        }
        if(userEntity != null) {
            // the user exists and the pin is correct
            AuthenticationHistoryEntity authenticationHistoryEntity = AuthenticationHistoryEntity.builder()
                    .userId(userEntity.getId())
                    .userAgent(userAgent)
                    .ipAddress(ipAddress)
                    .isAuthenticationResultSuccess(true)
                    .authenticationType(AuthenticationType.LOGIN)
                    .build();
            authenticationHistoryService.save(authenticationHistoryEntity);
        }
        if(userEntity.getIsLocked()) {
            throw new BadCredentialsException("Your account has been locked");
        }
        // update the last login time of user
        userEntity.setLastLoginDate(LocalDateTime.now());
        userEntity.getLoginCounter().reset();
        userEntity.getSalt().refreshSalt();
        updateUser(userEntity);
        jwtUtil.setSalt(userEntity.getSalt().getSalt());
        final UserDetails userDetails = customUserDetailService.loadUserByUsername(authenticationRequest.getMobileNumber());
        final String jwt = jwtUtil.generateToken(userDetails, ipAddress, userAgent);
        return AuthenticationResponse.builder()
                .firstName("Jon")
                .lastName("Narva")
                .emailAddress(userEntity.getEmailAddress())
                .mobileNumber(userEntity.getMobileNumber())
                .jwt(jwt)
                .userID(userEntity.getId())
                .build();
    }

    @Override
    public Optional<UserEntity> findUserById(String userId) {
        return userEntityRepository.findById(userId);
    }

    @Override
    public void updatePIN(String oldPIN, String newPIN) throws Exception {
        UserEntity user = securityUtil.getUserEntity();
        String hashedUserPassword = user.getPin();
        if(!passwordEncoder.matches(oldPIN, hashedUserPassword)) {
            throw new Exception("The PIN you provided does not much your account PIN");
        }
        String hashedNewPin = passwordEncoder.encode(newPIN);
        user.setPin(hashedNewPin);
        updateUser(user);
    }

    @Override
    public boolean validatePIN(String pin) {
        UserEntity user = securityUtil.getUserEntity();
        String hashedUserPassword = user.getPin();
        if(!passwordEncoder.matches(pin, hashedUserPassword)) {
            return false;
        }
        return true;
    }

}
