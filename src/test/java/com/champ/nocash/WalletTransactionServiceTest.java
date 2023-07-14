package com.champ.nocash;

import com.champ.nocash.collection.TransactionHistoryEntity;
import com.champ.nocash.collection.UserEntity;
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
    void depositFailSaveTransactionTest() throws Exception {
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
        when(transactionHistoryEntityService.save(any(TransactionHistoryEntity.class))).thenThrow(new Exception());

        doThrow(new Exception()).when(transactionHistoryEntityService).save(any(TransactionHistoryEntity.class));

        boolean result = walletTransactionService.deposit(amount, TransactionType.CASH_IN, payee, accountNumber);

        assertFalse(result);
        verify(securityUtil).getUserId();
        verify(walletRepository).findByUserId(userId);
        verify(walletRepository).save(wallet);
        verify(transactionHistoryEntityService).save(any(TransactionHistoryEntity.class));
    }

    @Test
    void withdrawSuccessTest() throws Exception {
        BigDecimal amount = new BigDecimal("100");
        String userId = "1";
        String payee = "Leonard";
        String accountNumber = "123";
        WalletEntity wallet = new WalletEntity();
        wallet.setUserId(userId);
        wallet.setBalance(new BigDecimal("100"));
        UserEntity userEntity = new UserEntity();

        when(securityUtil.getUserId()).thenReturn(userId);
        when(walletRepository.findByUserId(userId)).thenReturn(wallet);
        when(walletRepository.save(wallet)).thenReturn(wallet);
        when(transactionHistoryEntityService.save(any(TransactionHistoryEntity.class))).thenReturn(new TransactionHistoryEntity());

        boolean result = walletTransactionService.withdraw(amount, TransactionType.CASH_OUT, payee, accountNumber);

        assertTrue(result);
        verify(securityUtil).getUserId();
        verify(walletRepository).findByUserId(userId);
        verify(walletRepository).save(wallet);
        verify(transactionHistoryEntityService).save(any(TransactionHistoryEntity.class));
    }

    @Test
    void withdrawFailSaveTransactionTest() throws Exception {
        BigDecimal amount = new BigDecimal("100");
        String userId = "1";
        String payee = "Leonard";
        String accountNumber = "123";
        WalletEntity wallet = new WalletEntity();
        wallet.setUserId(userId);
        wallet.setBalance(new BigDecimal("100"));
        UserEntity userEntity = new UserEntity();

        when(securityUtil.getUserId()).thenReturn(userId);
        when(walletRepository.findByUserId(userId)).thenReturn(wallet);
        when(walletRepository.save(wallet)).thenReturn(wallet);
        when(transactionHistoryEntityService.save(any(TransactionHistoryEntity.class))).thenThrow(new Exception());

        doThrow(new Exception()).when(transactionHistoryEntityService).save(any(TransactionHistoryEntity.class));

        boolean result = walletTransactionService.withdraw(amount, TransactionType.CASH_IN, payee, accountNumber);

        assertFalse(result);
        verify(securityUtil).getUserId();
        verify(walletRepository).findByUserId(userId);
        verify(walletRepository).save(wallet);
        verify(transactionHistoryEntityService).save(any(TransactionHistoryEntity.class));
    }

    @Test
    void walletBalanceTest() {
        BigDecimal amount = new BigDecimal("100");
        String userId = "1";
        String payee = "Leonard";
        String accountNumber = "123";
        WalletEntity wallet = new WalletEntity();
        wallet.setUserId(userId);
        wallet.setBalance(new BigDecimal("100"));
        UserEntity userEntity = new UserEntity();

        when(securityUtil.getUserId()).thenReturn(userId);
        when(walletRepository.findByUserId(userId)).thenReturn(wallet);

        BigDecimal result = walletTransactionService.getBalance();

        assertEquals(wallet.getBalance(), result);
        verify(securityUtil).getUserId();
        verify(walletRepository).findByUserId(userId);
    }

    @Test
    void walletSaveTest() {
        WalletEntity wallet = new WalletEntity();

        when(walletRepository.save(wallet)).thenReturn(wallet);

        WalletEntity result = walletTransactionService.save(wallet);

        assertNotNull(result);
        assertEquals(wallet, result);
        verify(walletRepository).save(wallet);
    }

    @Test
    void transferTest() throws Exception {
        BigDecimal amount = new BigDecimal("100");
        String userId1 = "1";
        String userId2 = "2";
        UserEntity user1 = new UserEntity();
        UserEntity user2 = new UserEntity();
        user1.setId(userId1);
        user1.setMobileNumber("09111111111");
        user2.setId(userId2);
        user2.setMobileNumber("09222222222");
        WalletEntity wallet1 = new WalletEntity();
        WalletEntity wallet2 = new WalletEntity();
        wallet1.setUserId(userId1);
        wallet1.setBalance(new BigDecimal("101"));
        wallet2.setUserId(userId2);

        when(walletRepository.findByUserId(userId1)).thenReturn(wallet1);
        when(walletRepository.save(wallet1)).thenReturn(wallet1);
        when(walletRepository.save(wallet2)).thenReturn(wallet2);

        walletTransactionService.transfer(wallet2, amount, user2,user1);

        verify(walletRepository).findByUserId(userId1);
        verify(walletRepository).save(wallet1);
        verify(walletRepository).save(wallet2);
        verify(transactionHistoryEntityService, times(2)).saveAsIs(any(TransactionHistoryEntity.class));
    }

}
