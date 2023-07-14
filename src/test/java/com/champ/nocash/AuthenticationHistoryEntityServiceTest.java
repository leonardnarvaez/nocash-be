package com.champ.nocash;
import com.champ.nocash.collection.AuthenticationHistoryEntity;
import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.repository.AuthenticationHistoryRepository;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.impl.AuthenticationHistoryEntityServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class AuthenticationHistoryEntityServiceTest {

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private AuthenticationHistoryRepository authenticationHistoryRepository;

    @InjectMocks
    private AuthenticationHistoryEntityServiceImpl authenticationHistoryService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testSave() throws Exception {
        AuthenticationHistoryEntity authenticationHistoryEntity = new AuthenticationHistoryEntity();

        when(authenticationHistoryRepository.save(authenticationHistoryEntity)).thenReturn(authenticationHistoryEntity);

        AuthenticationHistoryEntity savedEntity = authenticationHistoryService.save(authenticationHistoryEntity);

        Assertions.assertEquals(authenticationHistoryEntity, savedEntity);
        verify(authenticationHistoryRepository, times(1)).save(authenticationHistoryEntity);
    }

    @Test
    public void testFindAll() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(String.valueOf(123L));

        List<AuthenticationHistoryEntity> expectedList = new ArrayList<>();
        expectedList.add(new AuthenticationHistoryEntity());

        when(securityUtil.getUserEntity()).thenReturn(userEntity);
        when(authenticationHistoryRepository.findByUserId(userEntity.getId())).thenReturn(expectedList);

        List<AuthenticationHistoryEntity> resultList = authenticationHistoryService.findAll();

        Assertions.assertEquals(expectedList, resultList);
        verify(securityUtil, times(1)).getUserEntity();
        verify(authenticationHistoryRepository, times(1)).findByUserId(userEntity.getId());
    }

    @Test
    public void testFindAllByDate() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(String.valueOf(123L));

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusHours(1);

        List<AuthenticationHistoryEntity> expectedList = new ArrayList<>();
        expectedList.add(new AuthenticationHistoryEntity());

        when(securityUtil.getUserId()).thenReturn(userEntity.getId());
        when(authenticationHistoryRepository.findByUserIdAndCreationTimeBetween(userEntity.getId(), startDate, endDate)).thenReturn(expectedList);

        List<AuthenticationHistoryEntity> resultList = authenticationHistoryService.findAllByDate(startDate, endDate);

        Assertions.assertEquals(expectedList, resultList);
        verify(securityUtil, times(1)).getUserId();
        verify(authenticationHistoryRepository, times(1)).findByUserIdAndCreationTimeBetween(userEntity.getId(), startDate, endDate);
    }

    @Test
    public void testFindAllByDate_throwsException() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(String.valueOf(123L));

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusHours(1);

        when(securityUtil.getUserId()).thenReturn(userEntity.getId());
        when(authenticationHistoryRepository.findByUserIdAndCreationTimeBetween(userEntity.getId(), startDate, endDate))
                .thenThrow(new RuntimeException("Error occurred while retrieving authentication history"));

        Assertions.assertThrows(Exception.class, () -> {
            authenticationHistoryService.findAllByDate(startDate, endDate);
        });

        verify(securityUtil, times(1)).getUserId();
        verify(authenticationHistoryRepository, times(1)).findByUserIdAndCreationTimeBetween(userEntity.getId(), startDate, endDate);
    }
}