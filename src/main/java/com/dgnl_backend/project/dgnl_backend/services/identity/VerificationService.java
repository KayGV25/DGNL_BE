package com.dgnl_backend.project.dgnl_backend.services.identity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.dgnl_backend.project.dgnl_backend.dtos.ResponseTemplate;
import com.dgnl_backend.project.dgnl_backend.dtos.user.response.LoginUserResponseDTO;
import com.dgnl_backend.project.dgnl_backend.exceptions.token.ExpiredOTPException;
import com.dgnl_backend.project.dgnl_backend.exceptions.token.InvalidOTPException;
import com.dgnl_backend.project.dgnl_backend.repositories.identity.TokenRepository;
import com.dgnl_backend.project.dgnl_backend.repositories.identity.UserDeviceRepository;
import com.dgnl_backend.project.dgnl_backend.repositories.identity.UserRepository;
import com.dgnl_backend.project.dgnl_backend.schemas.Email;
import com.dgnl_backend.project.dgnl_backend.schemas.identity.Token;
import com.dgnl_backend.project.dgnl_backend.schemas.identity.User;
import com.dgnl_backend.project.dgnl_backend.schemas.identity.UserDevice;
import com.dgnl_backend.project.dgnl_backend.services.RedisService;
import com.dgnl_backend.project.dgnl_backend.utils.JWTUtils;
import com.dgnl_backend.project.dgnl_backend.utils.SecurityUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Service for handling user verification processes, including email verification and OTP-based login.
 */
@Service
public class VerificationService {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private RedisService redisService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UserDeviceRepository userDeviceRepository;

    @Value("${backend.url}")
    private String backendUrl;

    /**
     * Sends an email verification link to the user.
     *
     * @param email The user's email address.
     * @return A success message indicating the email verification link was sent.
     * @apiNote If the email verification token is already stored in Redis, it reuses the same token.
     */
    public String sendVerificationEmail(String email) throws IOException {
        // Retrieve existing token from Redis
        String token = (String) redisTemplate.opsForValue().get(email);
        
        // If no token exists, generate a new one
        if (token == null) token = generateEmailVerificationToken(email);

        // Construct verification URL
        String verificationUrl = backendUrl + "/api/verification/account?token=" + token;

        // Prepare email content
        String subject = "[EMAIL] Email Verification";
        String templateContent = loadHtmlTemplate("ActivateAccountMail.html");
        String formatedContent = String.format(templateContent, verificationUrl, verificationUrl, verificationUrl);

        // Log verification URL (Replace with actual mail sender)
        System.out.println(verificationUrl);
        Email emailDetail = new Email(email, subject, formatedContent);
        emailService.sendEmail(emailDetail);
        return "Email verification link sent successfully. Please check your inbox for the link.";
    }

    /**
     * Generates a unique email verification token and stores it in Redis.
     *
     * @param email The user's email address.
     * @return The generated verification token.
     * @apiNote Token is stored in Redis for future verification.
     */
    public String generateEmailVerificationToken(String email) {
        // Generate unique token using UUID
        String token = UUID.randomUUID().toString();
        
        // Store token in Redis mapped to the email
        redisService.saveEmailVerificationToken(token, email);

        return token;
    }

    /**
     * Generates a 6-digit OTP for user authentication.
     *
     * @param user The user requesting OTP.
     * @return The generated OTP.
     * @apiNote The OTP is stored in Redis and sent via email.
     */
    @Transactional
    public String generateOtp(User user) {
        // Generate a random 6-digit OTP
        String otp = String.valueOf(100000 + new Random().nextInt(900000)); 

        // Store OTP in Redis linked to the user's username
        redisService.saveOtp(otp, user.getUsername());

        // Send OTP to the user's email
        sendOtpEmail(user.getEmail(), otp);

        return otp;
    }

    /**
     * Sends an email containing the OTP for authentication.
     *
     * @param email The recipient's email address.
     * @param otp   The OTP code.
     * @apiNote Intended to send OTP via JavaMailSender (not implemented in this snippet).
     */
    public void sendOtpEmail(String email, String otp) {
        try {
                // Prepare email content
            String subject = "[OTP] Your OTP: " + otp;
            String templateContent = loadHtmlTemplate("OTPMail.html");
            String formatedContent = String.format(templateContent, otp);

            // Placeholder for sending email
            // Use JavaMailSender to send email
            Email emailDetail = new Email(email, subject, formatedContent);
            emailService.sendEmail(emailDetail);
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String loadHtmlTemplate(String templateName) throws IOException {
        Path templatePath = new ClassPathResource("templates/" + templateName).getFile().toPath();
        return new String(Files.readAllBytes(templatePath), StandardCharsets.UTF_8);
    }

    /**
     * Verifies the account using the token from the verification email.
     *
     * @param token The verification token.
     * @return A response template indicating success or failure.
     * @throws InvalidOTPException If the token is invalid or expired.
     * @apiNote This method enables the user account upon successful verification.
     */
    @Transactional
    public ResponseTemplate<?> verifyAccount(@RequestParam String token) {
        // Retrieve the email associated with the token from Redis
        String email = (String) redisTemplate.opsForValue().get(token);

        // If token is not found, throw an exception
        if (email == null) throw new InvalidOTPException("Invalid or Expired token");
        
        // Fetch user from database using email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Enable user account
        user.setIsEnable(true);
        userRepository.save(user);

        // Remove token and email from Redis after successful verification
        redisTemplate.delete(token);
        redisTemplate.delete(email);

        return new ResponseTemplate<String>(null, "Account verified successfully");
    }

    /**
     * Verifies the OTP entered by the user and issues a JWT token upon success.
     *
     * @param username The user's username.
     * @param otp      The OTP entered by the user.
     * @return A response containing the JWT token and success message.
     * @throws ExpiredOTPException If the OTP has expired.
     * @throws InvalidOTPException If the OTP is incorrect.
     * @apiNote The OTP is deleted from Redis after verification.
     */
    @Transactional
    public ResponseTemplate<?> verifyOtp(String username, String otp, String deviceId, HttpServletRequest request) {
        // Retrieve the stored OTP from Redis
        String storedOtp = (String) redisTemplate.opsForValue().get("OTP_" + username);
    
        // Check if OTP is expired
        if (storedOtp == null) throw new ExpiredOTPException("OTP is Expired");

        // Validate the entered OTP
        if (!storedOtp.equals(otp)) throw new InvalidOTPException("Invalid OTP code");
        
        // Fetch user from database
        User user = userRepository.findByUsername(username).get();
        UUID userId = user.getId();

        // Get device fingerprint
        String fingerprint = SecurityUtils.getDeviceFingerprint(request);
        // Find if the device already exists for the user
        Optional<UserDevice> existingDeviceOpt = userDeviceRepository.findByUserAndFingerprintAndDeviceId(user, fingerprint, deviceId);

        UserDevice userDevice;
        if (existingDeviceOpt.isPresent()) {
            // Update existing device
            userDevice = existingDeviceOpt.get();
            if (!userDevice.getTrusted()) {
                userDevice.setTrusted(true);
                userDeviceRepository.save(userDevice);
            }
        }
        
        // Remove OTP from Redis after successful verification
        redisTemplate.delete("OTP_" + username);
        String token;
        if (!tokenRepository.existsByUserId(userId)){
            // Generate JWT token (valid for 1 year)
            token = jwtUtils.generate(userId.toString(), 1000 * (60 * 60 * 24 * 365));
            // Save the JWT token in the database
            tokenRepository.save(new Token(user, token));
        } else {
            token = tokenRepository.findByUserId(userId).get().getToken();
        }

        return new ResponseTemplate<LoginUserResponseDTO>(new LoginUserResponseDTO(token), "Login successful");
    }
}
