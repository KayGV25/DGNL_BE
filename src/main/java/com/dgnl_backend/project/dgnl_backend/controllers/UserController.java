package com.dgnl_backend.project.dgnl_backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.dgnl_backend.project.dgnl_backend.dtos.user.request.LoginUserDTO;
import com.dgnl_backend.project.dgnl_backend.dtos.user.request.NewUserDTO;
import com.dgnl_backend.project.dgnl_backend.dtos.user.response.LoginUserResponseDTO;
import com.dgnl_backend.project.dgnl_backend.exceptions.user.PasswordMissMatchException;
import com.dgnl_backend.project.dgnl_backend.exceptions.user.UserNotFoundException;
import com.dgnl_backend.project.dgnl_backend.schemas.User;
import com.dgnl_backend.project.dgnl_backend.services.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<List<User>> getUsersInfo() {
        return ResponseEntity.ok(userService.getUsersInfo());
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody NewUserDTO newUser) {
        try {
            userService.createUser(newUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists. Please choose a different username.");
        }
        return ResponseEntity.ok("User registered successfully");
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginUserResponseDTO> login(@RequestBody LoginUserDTO user) {
        try {
            LoginUserResponseDTO res = userService.login(user);
            return ResponseEntity.ok(res);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new LoginUserResponseDTO(null, "Invalid username"));
        } catch (PasswordMissMatchException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginUserResponseDTO(null, "Wrong password."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginUserResponseDTO(null, "An error occurred while logging in."));
        }
    }
}
