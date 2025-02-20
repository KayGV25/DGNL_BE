package com.dgnl_backend.project.dgnl_backend.services;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
import com.dgnl_backend.project.dgnl_backend.exceptions.token.TokenNotFoundException;
import com.dgnl_backend.project.dgnl_backend.exceptions.user.PasswordMissMatchException;
import com.dgnl_backend.project.dgnl_backend.exceptions.user.UserNotEnableException;
import com.dgnl_backend.project.dgnl_backend.exceptions.user.UserNotFoundException;
import com.dgnl_backend.project.dgnl_backend.repositories.GenderRepository;
import com.dgnl_backend.project.dgnl_backend.repositories.RoleRepository;
import com.dgnl_backend.project.dgnl_backend.repositories.TokenRepository;
import com.dgnl_backend.project.dgnl_backend.repositories.UserRepository;
import com.dgnl_backend.project.dgnl_backend.schemas.Gender;
import com.dgnl_backend.project.dgnl_backend.schemas.Role;
import com.dgnl_backend.project.dgnl_backend.schemas.User;
import com.dgnl_backend.project.dgnl_backend.utils.JWTUtils;
import com.dgnl_backend.project.dgnl_backend.utils.SecurityUtils;

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
    private JWTUtils jwtUtils; // Utility for JWT token management

    @Autowired
    private VerificationService verificationService; // Service for email verification
    @Autowired
    private RedisService redisService; // Service for managing OTP storage in Redis

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
    public void createUser(NewUserDTO newUser) {
        // Check if the username already exists
        if (userRepository.existsByUsername(newUser.username())) 
            throw new RuntimeException("Username already exists");
        if (userRepository.existsByEmail(newUser.email())) 
            throw new RuntimeException("Email already exists");
        // Convert date of birth from integers to SQL Date format
        LocalDate localDate = LocalDate.of(newUser.yob(), newUser.mob(), newUser.dob());
        Date dob = Date.valueOf(localDate);

        // Create a new user entity with hashed password
        User user = new User(
            newUser.username(),
            newUser.email(),
            SecurityUtils.hashPassword(newUser.password()),
            newUser.genderId(),
            dob,
            newUser.gradeLv(),
            newUser.roleId()
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
    public ResponseTemplate<?> login(LoginUserDTO loginUser) {
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
        if (!jwtUtils.isValid(tokenRepository.findByUserId(user.get().getId()).get().getToken())) {
            tokenRepository.deleteById(tokenRepository.findByUserId(user.get().getId()).get().getId());
            throw new InvalidJWTException("Invalid JWT Token");
        }
        // Check if a valid token already exists (to allow multi-device login)
        if (tokenRepository.existsByUserId(user.get().getId())) {
            return new ResponseTemplate<LoginUserResponseDTO>(
                new LoginUserResponseDTO(tokenRepository.findByUserId(user.get().getId()).get().getToken()), 
                "Login successful"
            );
        }

        // Generate and send OTP if token does not exist or is expired
        String otp = verificationService.generateOtp(user.get()); // Generate OTP

        // Save OTP in Redis with a 3-minute expiration
        redisService.saveOtp(otp, user.get().getUsername());

        return new ResponseTemplate<String>(null, "OTP sent to your email. Please verify to complete the login process.");
    }

    /**
     * Logs out a user by deleting their authentication token.
     * 
     * @param token The authentication token to be invalidated.
     * @return A string message confirming successful logout.
     * @throws TokenNotFoundException if the token does not exist.
     */
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

        // Retrieve gender information
        Optional<Gender> gender = genderRepository.findById(user.get().getGenderId());
        if (!gender.isPresent()) 
            throw new GenderNotFoundException("Gender not found with id: " + user.get().getGenderId());

        // Retrieve role information
        Optional<Role> role = roleRepository.findById(user.get().getRoleId());
        if (!role.isPresent()) 
            throw new RoleNotFoundException("Role not found with id: " + user.get().getRoleId());

        // Construct the user information response
        UserInfoResponseDTO userInfo = new UserInfoResponseDTO(
            user.get().getUsername(),
            gender.get().getGenderType(),
            new Date(user.get().getDob().getTime()),
            user.get().getToken(),
            user.get().getGradeLv(),
            role.get().getRoleName()
        );

        return new ResponseTemplate<UserInfoResponseDTO>(userInfo, "Success");
    }
}
