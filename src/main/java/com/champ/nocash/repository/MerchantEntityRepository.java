package com.champ.nocash.repository;

import com.champ.nocash.collection.MerchantEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantEntityRepository extends MongoRepository<MerchantEntity, String> {
}
