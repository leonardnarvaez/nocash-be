package com.champ.nocash;

import com.champ.nocash.collection.TransactionHistoryEntity;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.collection.Wallet;
import com.champ.nocash.entity.WalletEntity;
import com.champ.nocash.enums.TransactionType;
import com.champ.nocash.repository.WalletRepository;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.TransactionHistoryEntityService;
import com.champ.nocash.service.UserEntityService;
import com.champ.nocash.service.impl.WalletTransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WalletTransactionServiceTest {

    @Mock
    private UserEntityService userEntityService;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionHistoryEntityService transactionHistoryEntityService;

    @InjectMocks
    private WalletTransactionServiceImpl walletTransactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void depositSuccessTest() throws Exception {
        BigDecimal amount = new BigDecimal("100");
        String userId = "1";
        String payee = "Leonard";
        String accountNumber = "123";
        WalletEntity wallet = new WalletEntity();
        wallet.setUserId(userId);
        UserEntity userEntity = new UserEntity();

        when(securityUtil.getUserId()).thenReturn(userId);
        when(walletRepository.findByUserId(userId)).thenReturn(wallet);
        when(walletRepository.save(wallet)).thenReturn(wallet);
        when(transactionHistoryEntityService.save(any(TransactionHistoryEntity.class))).thenReturn(new TransactionHistoryEntity());

        boolean result = walletTransactionService.deposit(amount, TransactionType.CASH_IN, payee, accountNumber);

        assertTrue(result);
        verify(securityUtil).getUserId();
        verify(walletRepository).findByUserId(userId);
        verify(transactionHistoryEntityService).save(any(TransactionHistoryEntity.class));
    }

    @Test
    void depositFailUpdateUserTest() throws Exception {
        BigDecimal amount = new BigDecimal("100");
        String userId = "1";
        String payee = "Leonard";
        String accountNumber = "123";
        WalletEntity wallet = new WalletEntity();
        wallet.setUserId(userId);
        UserEntity userEntity = new UserEntity();

        when(securityUtil.getUserId()).thenReturn(userId);
        when(walletRepository.findByUserId(userId)).thenReturn(wallet);
        when(walletRepository.save(wallet)).thenThrow(new Exception());

        boolean result = walletTransactionService.deposit(amount, TransactionType.CASH_IN, payee, accountNumber);

        assertFalse(result);
        verify(securityUtil).getUserEntity();
        verify(userEntityService).updateUser(userEntity);
        verify(transactionHistoryEntityService, never()).save(any(TransactionHistoryEntity.class));
    }

    @Test
    void depositFailSaveTransactionTest() throws Exception {
        BigDecimal amount = new BigDecimal("100");
        String payee = "Leonard";
        String accountNumber = "123";
        Wallet wallet = new Wallet();
        UserEntity userEntity = new UserEntity();
        userEntity.setWallet(wallet);

        when(securityUtil.getUserEntity()).thenReturn(userEntity);
        when(userEntityService.updateUser(userEntity)).thenReturn(userEntity);
        when(transactionHistoryEntityService.save(any(TransactionHistoryEntity.class))).thenThrow(new Exception());

        boolean result = walletTransactionService.deposit(amount, TransactionType.CASH_IN, payee, accountNumber);

        assertFalse(result);
        verify(securityUtil).getUserEntity();
        verify(userEntityService).updateUser(userEntity);
        verify(transactionHistoryEntityService).save(any(TransactionHistoryEntity.class));
    }

    @Test
    void withdrawSuccessTest() throws Exception {
        BigDecimal amount = new BigDecimal("0");
        String payee = "Leonard";
        String accountNumber = "123";
        Wallet wallet = new Wallet();
        UserEntity userEntity = new UserEntity();
        userEntity.setWallet(wallet);

        when(securityUtil.getUserEntity()).thenReturn(userEntity);
        when(userEntityService.updateUser(userEntity)).thenReturn(userEntity);
        when(transactionHistoryEntityService.save(any(TransactionHistoryEntity.class))).thenReturn(new TransactionHistoryEntity());

        boolean result = walletTransactionService.withdraw(amount, TransactionType.CASH_OUT, payee, accountNumber);

        verify(securityUtil).getUserEntity();
        verify(userEntityService).updateUser(userEntity);
        verify(transactionHistoryEntityService).save(any(TransactionHistoryEntity.class));
    }

    @Test
    void withdrawFailUpdateUserTest() throws Exception {
        BigDecimal amount = new BigDecimal("0");
        String payee = "Leonard";
        String accountNumber = "123";
        Wallet wallet = new Wallet();
        UserEntity userEntity = new UserEntity();
        userEntity.setWallet(wallet);

        when(securityUtil.getUserEntity()).thenReturn(userEntity);
        when(userEntityService.updateUser(userEntity)).thenThrow(new Exception());

        boolean result = walletTransactionService.withdraw(amount, TransactionType.CASH_OUT, payee, accountNumber);

        assertFalse(result);
        verify(securityUtil).getUserEntity();
        verify(userEntityService).updateUser(userEntity);
        verify(transactionHistoryEntityService, never()).save(any(TransactionHistoryEntity.class));
    }

    @Test
    void withdrawFailSaveTransactionTest() throws Exception {
        BigDecimal amount = new BigDecimal("0");
        String payee = "Leonard";
        String accountNumber = "123";
        Wallet wallet = new Wallet();
        UserEntity userEntity = new UserEntity();
        userEntity.setWallet(wallet);

        when(securityUtil.getUserEntity()).thenReturn(userEntity);
        when(userEntityService.updateUser(userEntity)).thenReturn(userEntity);
        when(transactionHistoryEntityService.save(any(TransactionHistoryEntity.class))).thenThrow(new Exception());

        boolean result = walletTransactionService.withdraw(amount, TransactionType.CASH_OUT, payee, accountNumber);

        assertFalse(result);
        verify(securityUtil).getUserEntity();
        verify(userEntityService).updateUser(userEntity);
        verify(transactionHistoryEntityService).save(any(TransactionHistoryEntity.class));
    }

    @Test
    void walletBalanceTest() throws Exception {
        BigDecimal balance = new BigDecimal("0.0");
        Wallet wallet = new Wallet();
        UserEntity userEntity = new UserEntity();
        userEntity.setWallet(wallet);

        when(securityUtil.getUserEntity()).thenReturn(userEntity);

        BigDecimal result = walletTransactionService.getBalance();

        assertEquals(balance, result);
        verify(securityUtil).getUserEntity();

    }

}
