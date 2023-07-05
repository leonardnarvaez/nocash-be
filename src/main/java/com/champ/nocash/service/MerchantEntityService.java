package com.champ.nocash.service;

import com.champ.nocash.collection.MerchantEntity;

import java.util.List;
import java.util.Optional;

public interface MerchantEntityService {
    MerchantEntity save(MerchantEntity merchant) throws Exception;
    Optional<MerchantEntity> findById(String id);
    List<MerchantEntity> findAll() throws Exception;
}
