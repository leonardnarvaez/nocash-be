package com.champ.nocash.dal;

import com.champ.nocash.collection.Verification;
import org.springframework.data.mongodb.core.query.Query;

public interface UserDAL {
    Query getUserQuery(String userId);
    Boolean isActive(String userId);
    Boolean isLocked(String userId);
    Verification getVerification(String userId);
    void setIsActive(String userId, boolean isActive);
    void setIsLocked(String userId, boolean isLocked);
    void setVerification(String userId, Verification verification);
}
