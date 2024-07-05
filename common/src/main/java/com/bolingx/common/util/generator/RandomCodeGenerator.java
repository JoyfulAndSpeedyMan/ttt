package com.bolingx.common.util.generator;

import java.security.SecureRandom;

public class RandomCodeGenerator {
    private static final String DIGITS = "0123456789";

    private static final SecureRandom random = new SecureRandom();

    public static String generateCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be a positive integer.");
        }

        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(DIGITS.length());
            code.append(DIGITS.charAt(index));
        }

        return code.toString();
    }

}
