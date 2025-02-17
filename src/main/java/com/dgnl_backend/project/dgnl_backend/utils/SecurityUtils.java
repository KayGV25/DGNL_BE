package com.dgnl_backend.project.dgnl_backend.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class SecurityUtils {
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash); // Java 17+
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 Algorithm not found", e);
        }
    }

    public static Boolean matchesPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }

}
