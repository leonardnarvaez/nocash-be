package com.champ.nocash.util;

import java.util.UUID;

public class NumberGeneratorUtil {
    public static String generateRandomId() {
        UUID uuid = UUID.randomUUID();
        String randomId = uuid.toString().replace("-", "");
        return randomId;
    }
}
