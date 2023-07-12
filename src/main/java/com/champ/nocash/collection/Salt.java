package com.champ.nocash.collection;

import com.champ.nocash.util.UUIDUtil;
public class Salt {
    private String userSalt;
    public Salt() {
        refreshSalt();
    }

    public void refreshSalt() {
        userSalt = UUIDUtil.generateUniqueIdAsString();
    }

    public String getSalt() {
        return userSalt;
    }
}
