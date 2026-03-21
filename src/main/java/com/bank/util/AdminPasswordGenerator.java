package com.bank.util;

import java.security.SecureRandom;

public class AdminPasswordGenerator {

    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";

    private static final String ALL_CHARS = LOWER + UPPER + DIGITS;

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generate() {

        StringBuilder password = new StringBuilder();

        // Ensure at least one of each type
        password.append(LOWER.charAt(secureRandom.nextInt(LOWER.length())));
        password.append(UPPER.charAt(secureRandom.nextInt(UPPER.length())));
        password.append(DIGITS.charAt(secureRandom.nextInt(DIGITS.length())));

        // Fill remaining characters
        for (int i = 3; i < 8; i++) {
            int index = secureRandom.nextInt(ALL_CHARS.length());
            password.append(ALL_CHARS.charAt(index));
        }

        // Shuffle the password
        return shuffle(password.toString());
    }

    private static String shuffle(String input) {
        char[] array = input.toCharArray();

        for (int i = array.length - 1; i > 0; i--) {
            int index = secureRandom.nextInt(i + 1);

            char temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }

        return new String(array);
    }
}