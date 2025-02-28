package com.dgnl_backend.project.dgnl_backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.dgnl_backend.project.dgnl_backend.dtos.user.request.LoginUserDTO;
import com.dgnl_backend.project.dgnl_backend.dtos.user.request.NewUserDTO;
import com.dgnl_backend.project.dgnl_backend.exceptions.token.InvalidJWTException;
import com.dgnl_backend.project.dgnl_backend.exceptions.token.SendingOTPException;
import com.dgnl_backend.project.dgnl_backend.exceptions.user.PasswordMissMatchException;
import com.dgnl_backend.project.dgnl_backend.exceptions.user.UserNotEnableException;
import com.dgnl_backend.project.dgnl_backend.exceptions.user.UserNotFoundException;
import com.dgnl_backend.project.dgnl_backend.services.identity.UserService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;





@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(userService.getUserInfo(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody NewUserDTO newUser) {
        try {
            userService.createUser(newUser);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No template found for user registration. Please contact the administrator.");
        }
        return ResponseEntity.ok("User registered successfully. Please check your email to activate");
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserDTO user, HttpServletRequest request, @CookieValue("deviceId") String deviceId) {
        try {
            return ResponseEntity.ok(userService.login(user, deviceId, request));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } catch (PasswordMissMatchException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong password.");
        } catch (UserNotEnableException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (InvalidJWTException e){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        } catch (SendingOTPException e){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while logging in. ");
        }
    }
}
