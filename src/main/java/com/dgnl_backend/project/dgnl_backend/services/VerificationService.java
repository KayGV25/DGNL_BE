package com.dgnl_backend.project.dgnl_backend.services;

import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.dgnl_backend.project.dgnl_backend.dtos.ResponseTemplate;
import com.dgnl_backend.project.dgnl_backend.dtos.user.response.LoginUserResponseDTO;
import com.dgnl_backend.project.dgnl_backend.exceptions.token.ExpiredOTPException;
import com.dgnl_backend.project.dgnl_backend.exceptions.token.InvalidOTPException;
import com.dgnl_backend.project.dgnl_backend.repositories.TokenRepository;
import com.dgnl_backend.project.dgnl_backend.repositories.UserRepository;
import com.dgnl_backend.project.dgnl_backend.schemas.Token;
import com.dgnl_backend.project.dgnl_backend.schemas.User;
import com.dgnl_backend.project.dgnl_backend.utils.JWTUtils;

@Service
public class VerificationService {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    public void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        redisService.saveEmailVerificationToken(token, user.getEmail());
        String verificationUrl = "http://your-domain.com/api/verify?token=" + token;
        String subject = "Email Verification";
        String content = "Click the link to verify your email: " + verificationUrl;
        System.out.println(verificationUrl);
        // Use JavaMailSender to send email
    }

    @Transactional
    public String generateOtp(User user) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000)); // 6-digit OTP
        System.out.println("OTP: " + otp);
        redisService.saveOtp(otp, user.getUsername());
        sendOtpEmail(user.getEmail(), otp);
        return otp;
    }

    public void sendOtpEmail(String email, String otp) {
        String subject = "6 Digit OTP for Verification";
        String content = "Your OTP: " + otp;
        // Use JavaMailSender to send email
    }

    @Transactional
    public ResponseEntity<?> verifyAccount(@RequestParam String token) {
        String email = (String) redisTemplate.opsForValue().get(token);

        if (email == null) throw new InvalidOTPException("Invalid or Expired token");
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setIsEnable(true);
        userRepository.save(user);

        redisTemplate.delete(token); // Invalidate token after verification

        return ResponseEntity.ok("Account verified successfully");
    }

    @Transactional
    public ResponseTemplate<?> verifyOtp(String username, String otp) {
        String storedOtp = (String) redisTemplate.opsForValue().get("OTP_" + username);
    
        if (storedOtp == null) throw new ExpiredOTPException("OTP is Expired");
        if (!storedOtp.equals(otp)) throw new InvalidOTPException("Ivalid OTP code");
        
        User user = userRepository.findByUsername(username).get();
        UUID userId = user.getId();

        redisTemplate.delete("OTP_" + username); // Invalidate OTP after verification
        String token = jwtUtils.generate(userId.toString(), 1000*(60*60*24*365));
        tokenRepository.save(new Token(userId, token));
        return new ResponseTemplate<LoginUserResponseDTO>(new LoginUserResponseDTO(token), "Login successful");
    }

}
