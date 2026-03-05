package com.bank.util;

import java.util.Random;

public class PinGenerator {

    private static final Random random = new Random();

    public static String generate() {

        int pin = random.nextInt(10000); // 0 - 9999

        return String.format("%04d", pin); // ensures leading zeros
    }
}
