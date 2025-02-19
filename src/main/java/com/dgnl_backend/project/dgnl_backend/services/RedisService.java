package com.dgnl_backend.project.dgnl_backend.services;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void saveEmailVerificationToken(String token, String email) {
        redisTemplate.opsForValue().set(token, email, Duration.ofHours(3));
        redisTemplate.opsForValue().set(email, token, Duration.ofHours(3));
    }

    public void saveOtp(String otp, String username) {
        redisTemplate.opsForValue().set("OTP_" + username, otp, Duration.ofMinutes(3));
    }
}
