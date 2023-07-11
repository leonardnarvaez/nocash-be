package com.champ.nocash.service;

import com.champ.nocash.collection.Verification;

import java.io.IOException;

public interface VerificationService {
    boolean saveVerification(Verification verification);
    boolean requestEmailVerification(String email) throws Exception;
    boolean requestAccountReactivation(String email) throws Exception;
    boolean verifyEmail(String email, String code) throws Exception;
    boolean reactivateAccount(String email, String code, String newPIN, String ipAddress, String userAgent) throws Exception;
    void pinReset(String oldPIN, String newPIN, String ipAddress, String userAgent) throws Exception;
}
