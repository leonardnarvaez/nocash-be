package com.champ.nocash.service;

import com.champ.nocash.collection.AuthenticationHistoryEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface AuthenticationHistoryService {
    AuthenticationHistoryEntity save(AuthenticationHistoryEntity authenticationHistoryEntity) throws Exception;
    List<AuthenticationHistoryEntity> findAll();
    List<AuthenticationHistoryEntity> findAllByDate(LocalDateTime startDate, LocalDateTime endDate);
}
