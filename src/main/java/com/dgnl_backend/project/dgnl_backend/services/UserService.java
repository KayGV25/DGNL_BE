package com.dgnl_backend.project.dgnl_backend.services;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dgnl_backend.project.dgnl_backend.dtos.ResponseTemplate;
import com.dgnl_backend.project.dgnl_backend.dtos.user.request.LoginUserDTO;
import com.dgnl_backend.project.dgnl_backend.dtos.user.request.NewUserDTO;
import com.dgnl_backend.project.dgnl_backend.dtos.user.response.LoginUserResponseDTO;
import com.dgnl_backend.project.dgnl_backend.dtos.user.response.UserInfoResponseDTO;
import com.dgnl_backend.project.dgnl_backend.exceptions.gender.GenderNotFoundException;
import com.dgnl_backend.project.dgnl_backend.exceptions.role.RoleNotFoundException;
import com.dgnl_backend.project.dgnl_backend.exceptions.token.InvalidJWTException;
import com.dgnl_backend.project.dgnl_backend.exceptions.token.SendingOTPException;
import com.dgnl_backend.project.dgnl_backend.exceptions.token.TokenNotFoundException;
import com.dgnl_backend.project.dgnl_backend.exceptions.user.EmailInvalidException;
import com.dgnl_backend.project.dgnl_backend.exceptions.user.PasswordMissMatchException;
import com.dgnl_backend.project.dgnl_backend.exceptions.user.UserNotEnableException;
import com.dgnl_backend.project.dgnl_backend.exceptions.user.UserNotFoundException;
import com.dgnl_backend.project.dgnl_backend.repositories.GenderRepository;
import com.dgnl_backend.project.dgnl_backend.repositories.RoleRepository;
import com.dgnl_backend.project.dgnl_backend.repositories.TokenRepository;
import com.dgnl_backend.project.dgnl_backend.repositories.UserDeviceRepository;
import com.dgnl_backend.project.dgnl_backend.repositories.UserRepository;
import com.dgnl_backend.project.dgnl_backend.schemas.Gender;
import com.dgnl_backend.project.dgnl_backend.schemas.Role;
import com.dgnl_backend.project.dgnl_backend.schemas.Token;
import com.dgnl_backend.project.dgnl_backend.schemas.User;
import com.dgnl_backend.project.dgnl_backend.schemas.UserDevice;
import com.dgnl_backend.project.dgnl_backend.utils.JWTUtils;
import com.dgnl_backend.project.dgnl_backend.utils.PatternMatching;
import com.dgnl_backend.project.dgnl_backend.utils.SecurityUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Service class responsible for user-related operations such as registration, 
 * authentication, and retrieving user details.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository; // Repository for user management
    @Autowired
    private TokenRepository tokenRepository; // Repository for authentication tokens
    @Autowired
    private GenderRepository genderRepository; // Repository for gender data
    @Autowired
    private RoleRepository roleRepository; // Repository for roles
    @Autowired
    private UserDeviceRepository userDeviceRepository;

    @Autowired
    private JWTUtils jwtUtils; // Utility for JWT token management

    @Autowired
    private VerificationService verificationService; // Service for email verification
    @Autowired
    private RedisService redisService; // Service for managing OTP storage in Redis
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    /**
     * Retrieves a list of all users from the database.
     * 
     * @return A list of {@link User} objects.
     */
    public List<User> getUsersInfo() {
        return userRepository.findAll();
    }

    /**
     * Creates a new user account, hashes the password, stores user details, 
     * and sends a verification email.
     * 
     * @param newUser The {@link NewUserDTO} containing user registration details.
     * @throws RuntimeException if the username already exists.
     */
    @Transactional
    public void createUser(NewUserDTO newUser) throws IOException {
        // Validate email format using RFC 5322 standards
        if (!PatternMatching.patternMatches(newUser.email(), EMAIL_REGEX))
            throw new EmailInvalidException("Invalid email format");
        // Check if the username already exists
        if (userRepository.existsByUsername(newUser.username())) 
            throw new RuntimeException("Username already exists");
        if (userRepository.existsByEmail(newUser.email())) 
            throw new RuntimeException("Email already exists");
        // Convert date of birth from integers to SQL Date format
        LocalDate localDate = LocalDate.of(newUser.yob(), newUser.mob(), newUser.dob());
        Date dob = Date.valueOf(localDate);

        Optional<Gender> gender = genderRepository.findById(newUser.genderId());
        if (!gender.isPresent())
            throw new GenderNotFoundException("Gender not found");

        Optional<Role> roleOpt = roleRepository.findById(newUser.roleId());
        Role role;
        if (!roleOpt.isPresent()){
            role = roleRepository.getReferenceById(3);
        }
        else {
            role = roleOpt.get();
        }
        // Create a new user entity with hashed password
        User user = new User(
            newUser.username(),
            newUser.email(),
            SecurityUtils.hashPassword(newUser.password()),
            gender.get(),
            dob,
            newUser.gradeLv(),
            role
        );

        // Save user to the database
        userRepository.save(user);

        // Send email verification
        verificationService.sendVerificationEmail(user.getEmail());
    }

    /**
     * Authenticates a user, verifies password, and handles token management.
     * If a valid token exists, it is reused; otherwise, OTP verification is required.
     * 
     * @param loginUser The {@link LoginUserDTO} containing login credentials.
     * @return A {@link ResponseTemplate} containing the authentication token or OTP message.
     * @throws UserNotFoundException if the username does not exist.
     * @throws UserNotEnableException if the user is disabled.
     * @throws PasswordMissMatchException if the password is incorrect.
     */
    @Cacheable("login")
    public ResponseTemplate<?> login(LoginUserDTO loginUser, String deviceId, HttpServletRequest request) {

        // Get device fingerprint
        String fingerprint = SecurityUtils.getDeviceFingerprint(request);

        // Retrieve user by username
        Optional<User> user = userRepository.findByUsernameOrEmail(loginUser.username(), loginUser.username());
        if (!user.isPresent()) 
            throw new UserNotFoundException("User does not exist");

        // Check if the user is enabled
        if (!user.get().getIsEnable()) 
            throw new UserNotEnableException("User is disabled");

        // Validate password
        if (!SecurityUtils.matchesPassword(loginUser.password(), user.get().getPassword())) 
            throw new PasswordMissMatchException("Invalid password");

        // check if jwt is valid, if not then delete the token and throw an error
        Optional<Token> token = tokenRepository.findByUserId(user.get().getId());

        if (token.isPresent() && !jwtUtils.isValid(token.get().getToken())) {
            tokenRepository.deleteById(tokenRepository.findByUserId(user.get().getId()).get().getId());
            throw new InvalidJWTException("Invalid JWT Token");
        }
        // Check if a valid token already exists (to allow multi-device login)
        // OTP will sent when
        // 1. Have Token and no device -> verifyOtp will enable device and sent token
        // 2. Have no Token and no device -> verifyOtp will generate token and enable device and sent
        // 3. Have no Token and have device -> verifyOtp will generate token and sent token
        // 4. device but not trusted
        // Sent token back when have token and device
        Optional<UserDevice> existingDevice = userDeviceRepository.findByUserAndFingerprintAndDeviceId(user.get(), fingerprint, deviceId);

        // Have no token and no device
        if (token.isEmpty() && UserDeviceService.isNoDevice(existingDevice, fingerprint)) {
            // create device
           if (existingDevice.isEmpty()){
                UserDevice newDevice = new UserDevice();
                newDevice.setUser(user.get());
                newDevice.setDeviceId(deviceId);
                newDevice.setFingerprint(fingerprint);
                newDevice.setTrusted(false); // Not trusted yet
                userDeviceRepository.save(newDevice);
           }
            
            // sending otp
            sendingOtp(user.get());
            throw new SendingOTPException("Sending OTP. Check your email");
        }

        // Have no token and have device
        // No need to make a new device just send the otp to make a new token
        if (token.isEmpty() && !UserDeviceService.isNoDevice(existingDevice, fingerprint)){
            // sending otp
            sendingOtp(user.get());
            throw new SendingOTPException("Sending OTP. Check your email");
        }

        // Have token and have no device
        if (token.isPresent() && UserDeviceService.isNoDevice(existingDevice, fingerprint)){
            if (UserDeviceService.isNoDevice(existingDevice, fingerprint)){
                UserDevice newDevice = new UserDevice();
                newDevice.setUser(user.get());
                newDevice.setDeviceId(deviceId);
                newDevice.setFingerprint(fingerprint);
                newDevice.setTrusted(false); // Not trusted yet
                userDeviceRepository.save(newDevice);
            }
            
            // sending otp
            sendingOtp(user.get());
            throw new SendingOTPException("Sending OTP. Check your email");
        }
        
        // Device not trusted
        if (!existingDevice.get().getTrusted()){
            // sending otp
            sendingOtp(user.get());
            throw new SendingOTPException("Sending OTP. Check your email");
        }

        // Have token and device
        return new ResponseTemplate<LoginUserResponseDTO>(
                new LoginUserResponseDTO(token.get().getToken()), 
                "Login successful"
            );
    }
    private void sendingOtp(User user){
        // Check if OTP in redis
        String otp = (String) redisTemplate.opsForValue().get("OTP_" + user.getUsername()); // Get OTP from Redis
        // Generate and send OTP if token does not exist or is expired
        if (otp == null){
            otp = verificationService.generateOtp(user); // Generate OTP
            redisService.saveOtp(otp, user.getUsername());
        }
        else {
            verificationService.sendOtpEmail(user.getEmail(), otp);
        }
    }

    /**
     * Logs out a user by deleting their authentication token.
     * 
     * @param token The authentication token to be invalidated.
     * @return A string message confirming successful logout.
     * @throws TokenNotFoundException if the token does not exist.
     */
    // Logout from all device
    @Transactional
    public String logout(String token) {
        // Check if the token exists
        if (tokenRepository.existsByToken(token)) {
            tokenRepository.deleteByToken(token);
            return "Logged out successfully";
        } else {
            throw new TokenNotFoundException("No token found");
        }
    }

    /**
     * Retrieves user information, including gender and role details.
     * Uses caching to optimize repeated requests.
     * 
     * @param userId The unique identifier of the user.
     * @return A {@link ResponseTemplate} containing the {@link UserInfoResponseDTO}.
     * @throws UserNotFoundException if the user does not exist.
     * @throws GenderNotFoundException if the gender is not found.
     * @throws RoleNotFoundException if the role is not found.
     */
    @Cacheable("user")
    public ResponseTemplate<UserInfoResponseDTO> getUserInfo(String userId) {
        // Retrieve user from the database
        Optional<User> user = userRepository.findById(UUID.fromString(userId));
        if (!user.isPresent()) 
            throw new UserNotFoundException("User not found with id: " + userId);
        // Construct the user information response
        UserInfoResponseDTO userInfo = new UserInfoResponseDTO(
            user.get().getUsername(),
            user.get().getGender().getGenderType(),
            new Date(user.get().getDob().getTime()),
            user.get().getToken(),
            user.get().getGradeLv(),
            user.get().getRole().getRoleName()
        );

        return new ResponseTemplate<UserInfoResponseDTO>(userInfo, "Success");
    }
}
