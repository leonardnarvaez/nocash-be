package com.champ.nocash;

import com.champ.nocash.collection.CardEntity;
import com.champ.nocash.enums.TransactionType;
import com.champ.nocash.service.WalletTransactionService;
import com.champ.nocash.service.CardEntityService;
import com.champ.nocash.service.UserEntityService;
import com.champ.nocash.service.impl.CardTransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class CardTransactionServiceTest {
    @Mock
    private WalletTransactionService walletTransactionService;

    @Mock
    private CardEntityService cardEntityService;

    @Mock
    private UserEntityService userEntityService;

    @InjectMocks
    private CardTransactionServiceImpl cardTransactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void cashInValidPinTest() throws Exception {
        BigDecimal amount = new BigDecimal("100");
        String cardId = "cardId";
        String pin = "1234";

        when(userEntityService.validatePIN(pin)).thenReturn(true);
        CardEntity cardEntity = new CardEntity();
        cardEntity.setName("cardName");
        when(cardEntityService.findCardById(cardId)).thenReturn(cardEntity);
        when(walletTransactionService.deposit(amount, TransactionType.CASH_IN, "cardName", "")).thenReturn(true);

        boolean result = cardTransactionService.cashIn(amount, cardId, pin);

        assertTrue(result);
        verify(userEntityService).validatePIN(pin);
        verify(cardEntityService).findCardById(cardId);
        verify(walletTransactionService).deposit(amount, TransactionType.CASH_IN, "cardName", "");
    }

    @Test
    void cashInInvalidPinTest() throws Exception {
        BigDecimal amount = new BigDecimal("100");
        String cardId = "cardId";
        String pin = "1234";

        when(userEntityService.validatePIN(pin)).thenReturn(false);

        assertThrows(Exception.class, () -> cardTransactionService.cashIn(amount, cardId, pin));
        verify(userEntityService).validatePIN(pin);
        verify(cardEntityService, never()).findCardById(cardId);
        verify(walletTransactionService, never()).deposit(any(BigDecimal.class), any(TransactionType.class), anyString(), anyString());
    }

    @Test
    void cashOutValidPinTest() throws Exception {
        BigDecimal amount = new BigDecimal("100");
        String cardId = "cardId";
        String pin = "1234";

        when(userEntityService.validatePIN(pin)).thenReturn(true);
        CardEntity cardEntity = new CardEntity();
        cardEntity.setName("cardName");
        when(cardEntityService.findCardById(cardId)).thenReturn(cardEntity);
        when(walletTransactionService.withdraw(amount, TransactionType.CASH_OUT, "cardName", "")).thenReturn(true);

        boolean result = cardTransactionService.cashOut(amount, cardId, pin);

        assertTrue(result);
        verify(userEntityService).validatePIN(pin);
        verify(cardEntityService).findCardById(cardId);
        verify(walletTransactionService).withdraw(amount, TransactionType.CASH_OUT, "cardName", "");
    }

    @Test
    void cashOutInvalidPinTest() throws Exception {
        BigDecimal amount = new BigDecimal("100");
        String cardId = "cardId";
        String pin = "1234";

        when(userEntityService.validatePIN(pin)).thenReturn(false);

        assertThrows(Exception.class, () -> cardTransactionService.cashOut(amount, cardId, pin));
        verify(userEntityService).validatePIN(pin);
        verify(cardEntityService, never()).findCardById(cardId);
        verify(walletTransactionService, never()).withdraw(any(BigDecimal.class), any(TransactionType.class), anyString(), anyString());
    }
}
