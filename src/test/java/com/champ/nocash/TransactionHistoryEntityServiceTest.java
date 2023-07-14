package com.champ.nocash;
import com.champ.nocash.collection.TransactionHistoryEntity;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.repository.TransactionHistoryEntityRepository;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.impl.TransactionHistoryEntityServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class TransactionHistoryEntityServiceTest {
    @Mock
    private TransactionHistoryEntityRepository transactionHistoryEntityRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private TransactionHistoryEntityServiceImpl transactionHistoryEntityService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveTransaction() throws Exception {
        // Mock the behavior of dependencies
        TransactionHistoryEntity transaction = new TransactionHistoryEntity();
        when(securityUtil.getUserEntity()).thenReturn(new UserEntity());
        when(transactionHistoryEntityRepository.save(transaction)).thenReturn(transaction);

        // Perform the test
        TransactionHistoryEntity savedTransaction = transactionHistoryEntityService.save(transaction);

        // Verify the result
        assertNotNull(savedTransaction);
        assertEquals(transaction, savedTransaction);

        // Verify that the mock methods were called as expected
        verify(securityUtil).getUserEntity();
        verify(transactionHistoryEntityRepository).save(transaction);
    }

    @Test
    public void testSaveTransaction_ThrowsException() throws Exception {
        // Mock the behavior of dependencies
        TransactionHistoryEntity transaction = new TransactionHistoryEntity();
        when(securityUtil.getUserEntity()).thenReturn(null);

        // Perform the test and verify that an exception is thrown
        assertThrows(Exception.class, () -> transactionHistoryEntityService.save(transaction));

        // Verify that the mock methods were called as expected
        verify(securityUtil).getUserEntity();
        verifyNoInteractions(transactionHistoryEntityRepository);
    }

    @Test
    public void testSaveAsIs() throws Exception {
        // Arrange
        TransactionHistoryEntity transaction = new TransactionHistoryEntity();
        when(transactionHistoryEntityRepository.save(transaction)).thenReturn(transaction);

        // Act
        TransactionHistoryEntity result = transactionHistoryEntityService.saveAsIs(transaction);

        // Assert
        Assertions.assertEquals(transaction, result);
        verify(transactionHistoryEntityRepository, times(1)).save(transaction);
    }

    @Test
    public void testGetAll() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        String userId = "user123";
        when(securityUtil.getUserId()).thenReturn(userId);
        List<TransactionHistoryEntity> expectedTransactions = new ArrayList<>();
        when(transactionHistoryEntityRepository.findByCreationTimeBetweenAndUserId(startDate, endDate, userId)).thenReturn(expectedTransactions);

        // Act
        List<TransactionHistoryEntity> result = transactionHistoryEntityService.getAll(startDate, endDate);

        // Assert
        Assertions.assertEquals(expectedTransactions, result);
        verify(securityUtil, times(1)).getUserId();
        verify(transactionHistoryEntityRepository, times(1)).findByCreationTimeBetweenAndUserId(startDate, endDate, userId);
    }

    @Test
    public void testGetTransactionHistory() {
        // Arrange
        Long id = 123L;
        String userId = "user123";
        when(securityUtil.getUserId()).thenReturn(userId);
        TransactionHistoryEntity expectedTransaction = new TransactionHistoryEntity();
        when(transactionHistoryEntityRepository.findByIdAndUserId(id, userId)).thenReturn(expectedTransaction);

        // Act
        TransactionHistoryEntity result = transactionHistoryEntityService.getTransactionHistory(id);

        // Assert
        Assertions.assertEquals(expectedTransaction, result);
        verify(securityUtil, times(1)).getUserId();
        verify(transactionHistoryEntityRepository, times(1)).findByIdAndUserId(id, userId);
    }
}
