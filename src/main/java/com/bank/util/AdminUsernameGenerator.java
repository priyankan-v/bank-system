package com.bank.util;

import java.security.SecureRandom;

public class AdminUsernameGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static String generate() {

        int number = random.nextInt(100000); // 0 - 99999

        return "A" + String.format("%05d", number);
    }
}