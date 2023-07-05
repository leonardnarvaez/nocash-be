package com.champ.nocash.service.impl;

import com.champ.nocash.collection.AuthenticationHistoryEntity;
import com.champ.nocash.collection.TransactionHistoryEntity;
import com.champ.nocash.repository.AuthenticationHistoryRepository;
import com.champ.nocash.security.SecurityUtil;
import com.champ.nocash.service.AuthenticationHistoryService;
import com.champ.nocash.service.TransactionHistoryEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthenticationHistoryEntityServiceImpl implements AuthenticationHistoryService {
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private AuthenticationHistoryRepository authenticationHistoryRepository;

    @Override
    public AuthenticationHistoryEntity save(AuthenticationHistoryEntity authenticationHistoryEntity) throws Exception {
        authenticationHistoryEntity.setIpAddress("192.168.0.69");
        authenticationHistoryEntity.setUserAgent("chrome windows 10");
        authenticationHistoryEntity.setCreationTime(LocalDateTime.now());
        return authenticationHistoryRepository.save(authenticationHistoryEntity);
    }
}
