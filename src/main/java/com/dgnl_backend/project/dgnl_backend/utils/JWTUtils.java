package com.dgnl_backend.project.dgnl_backend.utils;

import java.security.MessageDigest;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JWTUtils {

    @Value("${jwt.secret.key}")
    private String secret;

    public static SecretKeySpec createKey(String secretString) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return new SecretKeySpec(digest.digest(secretString.getBytes()),"AES");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String generate(String userId, String role, Integer ttls) {
        Date currentDate = new Date();
        long currentTime = currentDate.getTime();

        return Jwts.builder()
            .subject(userId)
            .claim("role", role) // Add role to the token
            .issuedAt(currentDate)
            .expiration(new Date(currentTime + ttls)) // TTL (e.g., 1 year)
            .signWith(createKey(secret)) // Sign token
            .compact();
    }

    public Claims decode(String token) {
        return (Claims)
            Jwts.parser()
            .decryptWith(createKey(secret))
            .build()
            .parse(token)
            .getPayload();
    }

    public Boolean isValid(String token) {
        Claims claims = decode(token);
        if(!claims.getExpiration().before(new Date())) return true;
        return false;
    }

}
