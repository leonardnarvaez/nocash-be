package com.champ.nocash.util;

import java.util.UUID;

public class UUIDUtil {
    public static UUID generateUniqueId() {
        return UUID.randomUUID();
    }

    public static String generateUniqueIdAsString() {
        return generateUniqueId().toString();
    }

    public static UUID toUUID(String id) {
        return UUID.fromString(id);
    }
}
