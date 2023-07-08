package com.champ.nocash.dal.impl;

import com.champ.nocash.collection.UserEntity;
import com.champ.nocash.collection.Verification;
import com.champ.nocash.dal.UserDAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class UserDALImpl implements UserDAL {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public Query getUserQuery(String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").where("id").is(userId));
        return query;
    }

    @Override
    public Boolean isActive(String userId) {
        return null;
    }

    @Override
    public Boolean isLocked(String userId) {
        return null;
    }

    @Override
    public Verification getVerification(String userId) {
        return null;
    }

    @Override
    public void setIsActive(String userId, boolean isActive) {
        Query query = getUserQuery(userId);
        Update update = new Update();
        update.set("isLocked", isActive);
        mongoTemplate.findAndModify(query, update, UserEntity.class);
    }

    @Override
    public void setIsLocked(String userId, boolean isLocked) {
        Query query = getUserQuery(userId);
        Update update = new Update();
        update.set("isActive", isLocked);
        mongoTemplate.findAndModify(query, update, UserEntity.class);
    }

    @Override
    public void setVerification(String userId, Verification verification) {
        Query query = getUserQuery(userId);
        Update update = new Update();
        update.set("verification", verification);
        mongoTemplate.findAndModify(query, update, UserEntity.class);
    }
}
