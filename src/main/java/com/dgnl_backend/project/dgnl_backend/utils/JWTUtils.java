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

    public String generate(String userId, Integer ttls) {
        Date currentDate = new Date();
        long currentTime = currentDate.getTime();
        return Jwts.builder()
            .subject(userId)
            .issuedAt(currentDate)
            .expiration(new Date(currentTime + ttls)) // ttl = 1 year
            .encryptWith(createKey(secret), Jwts.ENC.A256GCM)
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
}
