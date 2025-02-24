package com.dgnl_backend.project.dgnl_backend.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import jakarta.servlet.http.HttpServletRequest;

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

    public static String getDeviceFingerprint(HttpServletRequest request) {
        // TODO modifying fingerprint's components
        String userAgent = request.getHeader("User-Agent");
        String acceptLang = request.getHeader("Accept-Language");
        String screenResolution = request.getHeader("Screen-Resolution"); // This needs to be passed from frontend
        String timezone = request.getHeader("Time-Zone"); // Also passed from frontend

        String rawFingerprint = userAgent + "|" + acceptLang + "|" + screenResolution + "|" + timezone;
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawFingerprint.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 Algorithm not found", e);
        }
    }

}
