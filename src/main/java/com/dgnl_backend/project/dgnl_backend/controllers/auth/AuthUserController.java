package com.dgnl_backend.project.dgnl_backend.controllers.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dgnl_backend.project.dgnl_backend.exceptions.token.TokenNotFoundException;
import com.dgnl_backend.project.dgnl_backend.services.identity.UserService;

@RestController
@RequestMapping("/auth/user")
public class AuthUserController {

    @Autowired
    private UserService userService;

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(userService.logout(token));
        } catch (TokenNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
