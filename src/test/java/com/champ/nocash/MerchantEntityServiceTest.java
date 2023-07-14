package com.champ.nocash;

import com.champ.nocash.collection.MerchantEntity;
import com.champ.nocash.repository.MerchantEntityRepository;
import com.champ.nocash.service.impl.MerchantEntityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class MerchantEntityServiceTest {

    @Mock
    private MerchantEntityRepository merchantEntityRepository;

    @InjectMocks
    private MerchantEntityServiceImpl merchantEntityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveSuccessTest() throws Exception {
        MerchantEntity merchant = new MerchantEntity();
        merchant.setCreatedAt(LocalDateTime.now());

        when(merchantEntityRepository.save(merchant)).thenReturn(merchant);

        MerchantEntity result = merchantEntityService.save(merchant);

        assertNotNull(result);
        assertEquals(merchant, result);
        assertNotNull(result.getCreatedAt());
        verify(merchantEntityRepository).save(merchant);
    }

    @Test
    void findByIdSuccessTest() {
        String id = "1";
        MerchantEntity merchant = new MerchantEntity();
        merchant.setId(id);

        when(merchantEntityRepository.findById(id)).thenReturn(Optional.of(merchant));

        Optional<MerchantEntity> result = merchantEntityService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(merchant, result.get());
        verify(merchantEntityRepository).findById(id);
    }

    @Test
    void findByIdFailTest() {
        String id = "1";

        when(merchantEntityRepository.findById(id)).thenReturn(Optional.empty());

        Optional<MerchantEntity> result = merchantEntityService.findById(id);

        assertFalse(result.isPresent());
        verify(merchantEntityRepository).findById(id);
    }

    @Test
    void findAllTest() throws Exception {
        MerchantEntity merchant1 = new MerchantEntity();
        MerchantEntity merchant2 = new MerchantEntity();
        merchant1.setName("Merchant 1");
        merchant2.setName("Merchant 2");

        List<MerchantEntity> merchantList = new ArrayList<>();

        merchantList.add(merchant1);
        merchantList.add(merchant2);

        when(merchantEntityRepository.findAll()).thenReturn(merchantList);

        List<MerchantEntity> result = merchantEntityService.findAll();

        assertEquals(merchantList, result);
        assertNotNull(result);
        assertTrue(result.contains(merchant1));
        assertTrue(result.contains(merchant2));

        verify(merchantEntityRepository).findAll();
    }

    @Test
    void findByMerchantIdSuccessTest() {
        String merchantId = "1";
        MerchantEntity merchant = new MerchantEntity();
        merchant.setMerchantId(merchantId);

        when(merchantEntityRepository.findByMerchantId(merchantId)).thenReturn(merchant);

        MerchantEntity result = merchantEntityService.findByMerchantId(merchantId);

        assertEquals(merchant, result);
    }
}
