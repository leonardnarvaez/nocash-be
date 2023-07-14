package com.champ.nocash.repository;

import com.champ.nocash.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {
    WalletEntity findByUserId(String userId);
}
