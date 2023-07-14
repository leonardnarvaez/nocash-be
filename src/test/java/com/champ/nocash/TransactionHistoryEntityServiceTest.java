package com.champ.nocash;
import com.champ.nocash.collection.TransactionHistoryEntity;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.repository.TransactionHistoryEntityRepository;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.impl.TransactionHistoryEntityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
}
