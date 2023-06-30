package com.champ.nocash.repository;

import com.champ.nocash.collection.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEntityRepository extends MongoRepository<UserEntity, String> {
    UserEntity findFirstByMobileNumber(String mobileNumber);
    UserEntity findFirstByEmailAddress(String email);
}
