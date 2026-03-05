package com.bank.util;

public class HashGenerator {

    public static void main(String[] args) {

        String rawPin = "1234";

        String hashed = PasswordUtil.hash(rawPin);

        System.out.println("Hashed PIN:");
        System.out.println(hashed);
    }
}