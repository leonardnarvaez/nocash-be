package com.champ.nocash.collection;
import java.util.Random;
import com.champ.nocash.enums.VerficationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Verification {
//    @Value("${verification.validity.minutes}")
    public static int VERIFICATION_VALIDITY_IN_MINUTES = 15;
//    @Value("${verification.retry.minutes}")
    public static int VERIFICATION_RETRY_IN_MINUTES = 1;
    private static Random rand = new Random();

    private String verificationCode;
    private LocalDateTime timestamp;
    private Boolean isUsed;
    private VerficationType verficationType;

    public Verification(String verificationCode, VerficationType verficationType) {
        isUsed = false;
        timestamp = LocalDateTime.now();
        this.verificationCode = verificationCode;
        this.verficationType = verficationType;
    }

    public void inValidate() {
        isUsed = true;
    }

    public boolean isExpired() {
        LocalDateTime expiryDate = timestamp.plusMinutes(VERIFICATION_VALIDITY_IN_MINUTES);
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(expiryDate);
    }

    public boolean canResend(VerficationType verficationType) {
        LocalDateTime resendDate = timestamp.plusMinutes(VERIFICATION_RETRY_IN_MINUTES);
        LocalDateTime now = LocalDateTime.now();
        if(isVerificationTypeEqual(verficationType)) {
            return now.isAfter(resendDate);
        }
        return true;
    }

    public boolean isCodeValid(String code) {
        return verificationCode.equals(code);
    }

    public boolean isVerificationTypeEqual(VerficationType verficationType) {
        return this.verficationType.equals(verficationType);
    }

    public boolean validate(String code, VerficationType verficationType) {
        return isCodeValid(code) && !isExpired() && isVerificationTypeEqual(verficationType);
    }

    private static String generateCode() {
        return String.valueOf(100_000 + rand.nextInt(899_999));
    }

    public static Verification generateEmailVerification() {
        return new Verification(generateCode(), VerficationType.EMAIL_VERIFICATION);
    }

    public static Verification generateAccountReactivation() {
        return new Verification(generateCode(), VerficationType.ACCOUNT_REACTIVATION);
    }
}
