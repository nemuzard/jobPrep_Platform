package com.jobprep.jobprep_platform.utils;

import java.util.Random;

public class RandomCodeUtil {
    public static String generateNumberCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));  
        }
        return sb.toString();
    }
}
