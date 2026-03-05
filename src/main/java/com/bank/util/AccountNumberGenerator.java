package com.bank.util;

import java.util.Random;

public class AccountNumberGenerator {

    private static final Random random = new Random();

    public static String generate() {

        int number = random.nextInt(90000000);

        return String.valueOf(number);
    }
}