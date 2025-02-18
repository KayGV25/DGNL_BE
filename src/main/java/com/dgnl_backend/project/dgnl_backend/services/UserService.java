package com.dgnl_backend.project.dgnl_backend.services;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dgnl_backend.project.dgnl_backend.dtos.ResponseTemplate;
import com.dgnl_backend.project.dgnl_backend.dtos.user.request.LoginUserDTO;
import com.dgnl_backend.project.dgnl_backend.dtos.user.request.NewUserDTO;
import com.dgnl_backend.project.dgnl_backend.dtos.user.response.LoginUserResponseDTO;
import com.dgnl_backend.project.dgnl_backend.dtos.user.response.UserInfoResponseDTO;
import com.dgnl_backend.project.dgnl_backend.exceptions.gender.GenderNotFoundException;
import com.dgnl_backend.project.dgnl_backend.exceptions.role.RoleNotFoundException;
import com.dgnl_backend.project.dgnl_backend.exceptions.token.TokenNotFoundException;
import com.dgnl_backend.project.dgnl_backend.exceptions.user.PasswordMissMatchException;
import com.dgnl_backend.project.dgnl_backend.exceptions.user.UserNotFoundException;
import com.dgnl_backend.project.dgnl_backend.repositories.GenderRepository;
import com.dgnl_backend.project.dgnl_backend.repositories.RoleRepository;
import com.dgnl_backend.project.dgnl_backend.repositories.TokenRepository;
import com.dgnl_backend.project.dgnl_backend.repositories.UserRepository;
import com.dgnl_backend.project.dgnl_backend.schemas.Gender;
import com.dgnl_backend.project.dgnl_backend.schemas.Role;
import com.dgnl_backend.project.dgnl_backend.schemas.Token;
import com.dgnl_backend.project.dgnl_backend.schemas.User;
import com.dgnl_backend.project.dgnl_backend.utils.JWTUtils;
import com.dgnl_backend.project.dgnl_backend.utils.SecurityUtils;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository; 
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private GenderRepository genderRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JWTUtils jwtUtils;


    public List<User> getUsersInfo() {
        return userRepository.findAll();
    }

    @Transactional
    public void createUser(NewUserDTO newUser) {
        if(userRepository.existsByUsername(newUser.username())) throw new RuntimeException("Username already exists");
        
        LocalDate localDate = LocalDate.of(newUser.yob(), newUser.mob(), newUser.dob());
        Date dob = Date.valueOf(localDate);
        User user = new User(
            newUser.username(),
            SecurityUtils.hashPassword(newUser.password()),
            newUser.genderId(),
            dob,
            newUser.gradeLv(),
            newUser.roleId()
            );
        userRepository.save(user);
    }

    public ResponseTemplate<LoginUserResponseDTO> login(LoginUserDTO loginUser) {
        Optional<User> user = userRepository.findByUsername(loginUser.username());
        if(!user.isPresent()) throw new UserNotFoundException("Username does not exist");
        if(!SecurityUtils.matchesPassword(loginUser.password(), user.get().getPassword())) throw new PasswordMissMatchException("Invalid password");
        // If token already exists, then return that token (enable multi device login, using the same token)
        if(tokenRepository.existsByUserId(user.get().getId())){
            return new ResponseTemplate<LoginUserResponseDTO>(
                new LoginUserResponseDTO(tokenRepository.findByUserId(user.get().getId()).getToken()), 
                "Login successful");
        }
        // If token does not exist, then generate a new token and save it in the database
        String token = jwtUtils.generate(user.get().getId().toString(), 1000*(60*60*24*365));
        tokenRepository.save(new Token(user.get().getId(), token));
        return new ResponseTemplate<LoginUserResponseDTO>(new LoginUserResponseDTO(token), "Login successful");
    }

    @Transactional
    public String logout(String token){
        if (tokenRepository.existsByToken(token)){
            tokenRepository.deleteByToken(token);
            return "Logged out successfully";
        } else throw new TokenNotFoundException("No token found");
    }

    public ResponseTemplate<UserInfoResponseDTO> getUserInfo(String userId) {
        Optional<User> user = userRepository.findById(UUID.fromString(userId));
        if (!user.isPresent()) throw new UserNotFoundException("User not found with id" + userId);
        Optional<Gender> gender = genderRepository.findById(user.get().getGenderId());
        if(!gender.isPresent()) throw new GenderNotFoundException("Gender not found with id" + user.get().getGenderId());
        Optional<Role> role = roleRepository.findById(user.get().getRoleId());
        if(!role.isPresent()) throw new RoleNotFoundException("Role not found with id" + user.get().getGenderId());
        UserInfoResponseDTO userInfo = new UserInfoResponseDTO(
            user.get().getUsername(), 
            gender.get().getGenderType(), 
            new Date(user.get().getDob().getTime()), 
            user.get().getToken(), 
            user.get().getGradeLv(),
            role.get().getRoleName());
        return new ResponseTemplate<UserInfoResponseDTO>(userInfo, "Success");
    }
}
