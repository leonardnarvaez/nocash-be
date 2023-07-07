package com.champ.nocash.util;

import java.util.Random;

public class ColorGeneratorUtil {
    private static final String[] COLORS = {
            "#FFB400",
            "#6236FF",
            "#8494A8",
            "#1DCC70",
            "#FF396F",
    };
    public static String getRandomColor() {
        Random random = new Random();
        int index = random.nextInt(COLORS.length);
        return COLORS[index];
    }
}
