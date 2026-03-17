package com.bank.util;

public class HashGenerator {

    public static void main(String[] args) {

        String rawPin = "Admin123";

        String hashed = PasswordUtil.hash(rawPin);

        System.out.println("Hashed PIN:");
        System.out.println(hashed);
    }

    // public static void main(String[] args) {

    // String rawPin = "1234";
    // String storedHash = "$2a$10$1a4kA0AzdAKNjWe1RDmztObib1EEhqJoR02nekD.MWAAV3QtfeO/y";

    // boolean valid = PasswordUtil.verify(rawPin, storedHash);

    // System.out.println(valid);
    // }
}