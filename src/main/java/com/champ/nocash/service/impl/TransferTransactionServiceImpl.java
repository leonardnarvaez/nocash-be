package com.champ.nocash.service.impl;

import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.entity.WalletEntity;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.TransferTransactionService;
import com.champ.nocash.service.UserEntityService;
import com.champ.nocash.service.WalletTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransferTransactionServiceImpl implements TransferTransactionService {
    @Autowired
    private UserEntityService userEntityService;
    @Autowired
    private WalletTransactionService walletTransactionService;
    @Autowired
    private SecurityUtil securityUtil;
    @Override
    public void transfer(String otherMobile, BigDecimal amount) throws Exception {
        if(otherMobile == null) {
            throw new IllegalArgumentException("Mobile number must not be null");
        }
        if(otherMobile.equals("")) {
            throw new IllegalArgumentException("Mobile number must not be an empty string");
        }
        UserEntity recipientUser = userEntityService.findUserByMobile(otherMobile);
        if(recipientUser == null) {
            throw new Exception("The recipient doesnt exists");
        }
        UserEntity currentUser = securityUtil.getUserEntity();
        if(currentUser.getMobileNumber().equals(recipientUser.getMobileNumber())) {
            throw new Exception("Self transfer haha");
        }
        WalletEntity recipientWallet = walletTransactionService.getWalletByUserId(recipientUser.getId());
        if(recipientWallet == null) {
            throw new Exception("recipient wallet does not exists");
        }
        walletTransactionService.transfer(recipientWallet, amount, recipientUser, currentUser);
    }
}
