package com.bank.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Hash PIN or password
    public static String hash(String raw) {
        return BCrypt.hashpw(raw, BCrypt.gensalt());
    }

    // Verify PIN or password
    public static boolean verify(String raw, String hashed) {
        return BCrypt.checkpw(raw, hashed);
    }
}