package com.champ.nocash.service;

import com.champ.nocash.collection.AuthenticationHistoryEntity;

public interface AuthenticationHistoryService {
    AuthenticationHistoryEntity save(AuthenticationHistoryEntity authenticationHistoryEntity) throws Exception;
}
