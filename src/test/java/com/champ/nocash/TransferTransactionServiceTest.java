package com.champ.nocash;

import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.entity.WalletEntity;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.UserEntityService;
import com.champ.nocash.service.WalletTransactionService;
import com.champ.nocash.service.impl.TransferTransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class TransferTransactionServiceTest {
    @Mock
    private UserEntityService userEntityService;

    @Mock
    private WalletTransactionService walletTransactionService;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private TransferTransactionServiceImpl transferTransactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void transferSuccessTest() throws Exception {
        BigDecimal amount = new BigDecimal("100");
        String pin = "1234";
        String userId1 = "1";
        String userId2 = "2";
        UserEntity user1 = new UserEntity();
        UserEntity user2 = new UserEntity();
        user1.setId(userId1);
        user1.setMobileNumber("09111111111");
        user2.setId(userId2);
        user2.setMobileNumber("09222222222");
        user1.setPin(pin);
        WalletEntity wallet1 = new WalletEntity();
        WalletEntity wallet2 = new WalletEntity();
        wallet1.setUserId(userId1);
        wallet1.setBalance(new BigDecimal("101"));
        wallet2.setUserId(userId2);

        when(userEntityService.findUserByMobile(user2.getMobileNumber())).thenReturn(user2);
        when(securityUtil.getUserEntity()).thenReturn(user1);
        when(walletTransactionService.getWalletByUserId(user2.getId())).thenReturn(wallet2);
        when(userEntityService.validatePIN(pin)).thenReturn(true);

        assertDoesNotThrow(() -> transferTransactionService.transfer(user2.getMobileNumber(), amount, pin));

        verify(userEntityService).findUserByMobile(user2.getMobileNumber());
        verify(securityUtil).getUserEntity();
        verify(walletTransactionService).getWalletByUserId(user2.getId());
    }
}
