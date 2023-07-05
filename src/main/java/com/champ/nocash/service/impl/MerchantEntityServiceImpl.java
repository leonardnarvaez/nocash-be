package com.champ.nocash.service.impl;

import com.champ.nocash.collection.MerchantEntity;
import com.champ.nocash.repository.MerchantEntityRepository;
import com.champ.nocash.service.MerchantEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MerchantEntityServiceImpl implements MerchantEntityService {

    @Autowired
    private MerchantEntityRepository merchantEntityRepository;
    @Override
    public MerchantEntity save(MerchantEntity merchant) throws Exception {
        merchant.setCreatedAt(LocalDateTime.now());
        return merchantEntityRepository.save(merchant);
    }

    @Override
    public Optional<MerchantEntity> findById(String id) {
        return merchantEntityRepository.findById(id);
    }

    @Override
    public List<MerchantEntity> findAll() throws Exception {
        return merchantEntityRepository.findAll();
    }

}
