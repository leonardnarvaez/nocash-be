package com.champ.nocash.service.impl;

import com.champ.nocash.collection.AuthenticationHistoryEntity;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.collection.Verification;
import com.champ.nocash.dal.UserDAL;
import com.champ.nocash.enums.AuthenticationType;
import com.champ.nocash.enums.VerficationType;
import com.champ.nocash.security.CustomUserDetailService;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.AuthenticationHistoryService;
import com.champ.nocash.service.UserEntityService;
import com.champ.nocash.service.VerificationService;
import com.champ.nocash.util.EmailMessageProvider;
import com.champ.nocash.util.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VerificationServiceImpl implements VerificationService {
    @Autowired
    private UserEntityService userEntityService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private UserDAL userDAL;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CustomUserDetailService customUserDetailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationHistoryService authenticationHistoryService;
    @Override
    public boolean saveVerification(Verification verification) {
        String userId = securityUtil.getUserId();
        try {
            userDAL.setVerification(userId, verification);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public boolean requestEmailVerification(String email) throws Exception {
        UserEntity userEntity = userEntityService.findUserByEmail(email);
        if(userEntity == null) {
            throw new UsernameNotFoundException(String.format("User with email %s is not found", email));
        }
        if(userEntity.getIsActive()) {
            throw new Exception("User account is already active");
        }
        Verification userVerification = userEntity.getVerification();
        if(!userVerification.canResend(VerficationType.EMAIL_VERIFICATION)) {
            throw new Exception("You cannot resend an email verification yet");
        }
        Verification verification = Verification.generateEmailVerification();
        userDAL.setVerification(userEntity.getId(), verification);
        String emailMessage = EmailMessageProvider.getEmailVerificationMessage("Jon Narva", verification.getVerificationCode());
        emailService.sendMIMEMessage(email, "Account Activation", emailMessage);
        return true;
    }

    @Override
    public boolean requestAccountReactivation(String email) throws Exception {
        UserEntity userEntity = userEntityService.findUserByEmail(email);
        if(userEntity == null) {
            throw new UsernameNotFoundException(String.format("User with email %s is not found", email));
        }
        if(!userEntity.getIsLocked()) {
            throw new Exception("User account is not locked");
        }
        Verification userVerification = userEntity.getVerification();
        if(!userVerification.canResend(VerficationType.ACCOUNT_REACTIVATION)) {
            throw new Exception("You cannot resend an account reactivation yet");
        }
        Verification verification = Verification.generateAccountReactivation();
        userDAL.setVerification(userEntity.getId(), verification);
        String emailMessage = EmailMessageProvider.getEmailVerificationMessage("Jon Narva", verification.getVerificationCode());
        emailService.sendMIMEMessage(email, "Account Reactivation", emailMessage);
        return true;
    }

    @Override
    public boolean verifyEmail(String email, String code) throws Exception {
        UserEntity userEntity = userEntityService.findUserByEmail(email);
        if(userEntity == null) {
            throw new UsernameNotFoundException(String.format("User with email %s is not found", email));
        }
        Verification verification = userEntity.getVerification();
        if(verification.isExpired()) {
            throw new Exception("No verification request");
        }
        if(!verification.isVerificationTypeEqual(VerficationType.EMAIL_VERIFICATION)) {
            throw new Exception("Verification Type Mismatch");
        }
        if(!verification.isCodeValid(code)) {
            throw new Exception("Invalid account activation code");
        }
        if(verification.getIsUsed()) {
            throw new Exception("Verification code already used");
        }
        userEntity.setIsActive(true);
        verification.inValidate();
        userEntityService.updateUser(userEntity);
        return true;
    }

    @Override
    public boolean reactivateAccount(String email, String code, String newPin, String ipAddress, String userAgent) throws Exception {
        UserEntity userEntity = userEntityService.findUserByEmail(email);
        if(userEntity == null) {
            throw new UsernameNotFoundException(String.format("User with email %s is not found", email));
        }
        if(!userEntity.getIsActive()) {
            throw new Exception("Account is not active");
        }
        Verification verification = userEntity.getVerification();
        if(verification.isExpired()) {
            throw new Exception("No verification request");
        }
        if(!verification.isVerificationTypeEqual(VerficationType.ACCOUNT_REACTIVATION)) {
            throw new Exception("Verification Type Mismatch");
        }
        if(!verification.isCodeValid(code)) {
            throw new Exception("Invalid account reactivation code");
        }
        if(verification.getIsUsed()) {
            throw new Exception("Verification code already used");
        }
        userEntity.setIsLocked(false);
        userEntity.setPin(passwordEncoder.encode(newPin));
        userEntity.getSalt().refreshSalt();
        verification.inValidate();
        userEntityService.updateUser(userEntity);
        AuthenticationHistoryEntity reactivate = AuthenticationHistoryEntity.builder()
                .userId(userEntity.getId())
                .isAuthenticationResultSuccess(true)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .authenticationType(AuthenticationType.ACCOUNT_REACTIVATION)
                .build();
        authenticationHistoryService.save(reactivate);
        return true;
    }

    @Override
    public void pinReset(String oldPIN, String newPIN, String ipAddress, String userAgent) throws Exception {
        UserEntity userEntity = securityUtil.getUserEntity();
        try {
            userEntityService.updatePIN(oldPIN, newPIN);
            userEntity = securityUtil.getUserEntity();
            userEntity.getLoginCounter().reset();
            userEntity.getSalt().refreshSalt();
            userEntityService.updateUser(userEntity);
        } catch (Exception e) {
            e.printStackTrace();
            AuthenticationHistoryEntity pinReset = AuthenticationHistoryEntity.builder()
                    .userId(userEntity.getId())
                    .isAuthenticationResultSuccess(false)
                    .authenticationType(AuthenticationType.PIN_RESET)
                    .build();
            authenticationHistoryService.save(pinReset);
            userEntity = securityUtil.getUserEntity();
            userEntity.getLoginCounter().increment();
            if(!userEntity.getLoginCounter().isValid() && !userEntity.getIsLocked()) {
                userEntity.setIsLocked(true);
                emailService.sendMIMEMessage(
                        userEntity.getEmailAddress(),
                        "Account Locked",
                        EmailMessageProvider.getAccountLockMessage("Jon narva", ipAddress, userAgent, LocalDateTime.now()));
            }
            userEntityService.updateUser(userEntity);
            if(userEntity.getIsLocked()) {
                throw new BadCredentialsException("Your account has been locked");
            }

            throw new Exception(e.getMessage());
        }
        AuthenticationHistoryEntity pinReset = AuthenticationHistoryEntity.builder()
                .userId(userEntity.getId())
                .isAuthenticationResultSuccess(true)
                .authenticationType(AuthenticationType.PIN_RESET)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        authenticationHistoryService.save(pinReset);
    }
}
