package com.champ.nocash.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmailMessageProvider {
    public static String getEmailVerificationMessage(String userName, String verificationCode) {
        StringBuilder message = new StringBuilder();

        message.append(String.format("<h2>Dear %s;</h2>", userName));
        message.append("<p>You have successfully registered to NoCash.</p>");
        message.append("<p>Please use the OTP below to activate your account. This OTP will expire in</p>");
        message.append(String.format("<b>%s</b>", verificationCode));
        return  message.toString();
    }
    public static String getReactivateAccountMessage(String userName, String accountID, String verificationCode, String ipAddress) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("<h2>Dear %s;</h2>", userName));
        message.append("<p>We received a request to reactivate your account.</p>");
        message.append("<p>If you didn't request this pin reset please ignore this email.</p>");
        message.append("<p>To reactivate your account please use the OTP below. The OTP only last for 15 minutes</p>");
        message.append(String.format("<b>%s</b>", verificationCode));
        return  message.toString();
    }
    public static String getAccountLockMessage(String userName, String ipAddress, String userAgent, LocalDateTime timestamp) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("<h2>Dear %s;</h2>", userName));
        message.append("<p>Your account has been locked</p>");
        message.append("We've received several failed login attempts from your account.");
        message.append(String.format("IP Address: %s, User Agent: %s, Timestamp: %s", ipAddress, userAgent, timestamp.toString()));
        message.append("You can request to reactivate your account");
        return  message.toString();
    }
}
