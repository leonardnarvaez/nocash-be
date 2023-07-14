package com.champ.nocash;

import com.champ.nocash.collection.MerchantEntity;
import com.champ.nocash.enums.TransactionType;
import com.champ.nocash.service.MerchantEntityService;
import com.champ.nocash.service.UserEntityService;
import com.champ.nocash.service.WalletTransactionService;
import com.champ.nocash.service.impl.BillPaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

public class BillPaymentServiceImplTest {
    @InjectMocks
    private BillPaymentServiceImpl billPaymentService;

    @Mock
    private WalletTransactionService walletTransactionService;

    @Mock
    private MerchantEntityService merchantEntityService;

    @Mock
    private UserEntityService userEntityService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPayBill_ValidMerchantAndValidPin_Success() throws Exception {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(100.0);
        String merchantId = "123";
        String accountNumber = "456";
        String pin = "789";

        MerchantEntity merchant = new MerchantEntity();
        merchant.setName("Some Merchant");

        when(merchantEntityService.findByMerchantId(merchantId)).thenReturn(merchant);
        when(userEntityService.validatePIN(pin)).thenReturn(true);
        when(walletTransactionService.withdraw(amount, TransactionType.PAY_BILL, merchant.getName(), accountNumber)).thenReturn(true);

        // Act
        boolean result = billPaymentService.payBill(amount, merchantId, accountNumber, pin);

        // Assert
        assertTrue(result);
        verify(merchantEntityService, times(1)).findByMerchantId(merchantId);
        verify(userEntityService, times(1)).validatePIN(pin);
        verify(walletTransactionService, times(1)).withdraw(amount, TransactionType.PAY_BILL, merchant.getName(), accountNumber);
    }
    @Test
    public void testPayBill_NoMerchantFound_ExceptionThrown() throws Exception {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(100.0);
        String merchantId = "123";
        String accountNumber = "456";
        String pin = "789";

        when(merchantEntityService.findByMerchantId(merchantId)).thenReturn(null);

        // Act and Assert
        assertThrows(Exception.class, () -> billPaymentService.payBill(amount, merchantId, accountNumber, pin));
        verify(merchantEntityService, times(1)).findByMerchantId(merchantId);
        verify(userEntityService, never()).validatePIN(anyString());
        verify(walletTransactionService, never()).withdraw(any(BigDecimal.class), any(TransactionType.class), anyString(), anyString());
    }

    @Test
    public void testPayBill_InvalidPin_ExceptionThrown() throws Exception {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(100.0);
        String merchantId = "123";
        String accountNumber = "456";
        String pin = "789";

        MerchantEntity merchant = new MerchantEntity();
        merchant.setName("Some Merchant");

        when(merchantEntityService.findByMerchantId(merchantId)).thenReturn(merchant);
        when(userEntityService.validatePIN(pin)).thenReturn(false);

        // Act and Assert
        assertThrows(Exception.class, () -> billPaymentService.payBill(amount, merchantId, accountNumber, pin));
        verify(merchantEntityService, times(1)).findByMerchantId(merchantId);
        verify(userEntityService, times(1)).validatePIN(pin);
        verify(walletTransactionService, never()).withdraw(any(BigDecimal.class), any(TransactionType.class), anyString(), anyString());
    }




}
