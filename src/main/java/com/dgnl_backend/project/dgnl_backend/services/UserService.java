package com.dgnl_backend.project.dgnl_backend.services;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dgnl_backend.project.dgnl_backend.dtos.user.request.LoginUserDTO;
import com.dgnl_backend.project.dgnl_backend.dtos.user.request.NewUserDTO;
import com.dgnl_backend.project.dgnl_backend.dtos.user.response.LoginUserResponseDTO;
import com.dgnl_backend.project.dgnl_backend.exceptions.user.PasswordMissMatchException;
import com.dgnl_backend.project.dgnl_backend.exceptions.user.UserNotFoundException;
import com.dgnl_backend.project.dgnl_backend.repositories.UserRepository;
import com.dgnl_backend.project.dgnl_backend.schemas.User;
import com.dgnl_backend.project.dgnl_backend.utils.JWTUtils;
import com.dgnl_backend.project.dgnl_backend.utils.SecurityUtils;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository; 

    @Autowired
    private JWTUtils jwtUtils;


    public List<User> getUsersInfo() {
        return userRepository.findAll();
    }

    public void createUser(NewUserDTO newUser) {
        if(userRepository.existsByUsername(newUser.username())) throw new RuntimeException("Username already exists");
        
        LocalDate localDate = LocalDate.of(newUser.yob(), newUser.mob(), newUser.dob());
        Date dob = Date.valueOf(localDate);
        User user = new User(
            newUser.username(),
            SecurityUtils.hashPassword(newUser.password()),
            newUser.genderId(),
            dob,
            newUser.gradeLv()
            );
        userRepository.save(user);
    }

    public LoginUserResponseDTO login(LoginUserDTO loginUser) {
        Optional<User> user = userRepository.findByUsername(loginUser.username());
        if(!user.isPresent()) throw new UserNotFoundException("Username does not exist");
        if(!SecurityUtils.matchesPassword(loginUser.password(), user.get().getPassword())) throw new PasswordMissMatchException("Invalid password");
        return new LoginUserResponseDTO(jwtUtils.generate(user.get().getId().toString()), "Login successful");
    }
}
