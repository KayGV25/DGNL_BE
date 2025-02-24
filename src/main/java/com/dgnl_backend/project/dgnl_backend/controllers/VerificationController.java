package com.dgnl_backend.project.dgnl_backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dgnl_backend.project.dgnl_backend.dtos.ResponseTemplate;
import com.dgnl_backend.project.dgnl_backend.services.VerificationService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/verification")
public class VerificationController {

    @Autowired
    private VerificationService verificationService;

    @GetMapping("/otp/{username}")
    public ResponseEntity<ResponseTemplate<?>> verifyOTP(
        @RequestParam String otp,
        @PathVariable String username,
        @CookieValue("deviceId") String deviceId,
        HttpServletRequest request
        ) {
        try {
            return ResponseEntity.ok(verificationService.verifyOtp(username, otp, deviceId, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseTemplate<>(null, e.getMessage()));
        }
    }

    @GetMapping("/account")
    public ResponseEntity<ResponseTemplate<?>> verifyAccount(
        @RequestParam String token
        ) {
            try {
                return ResponseEntity.ok(verificationService.verifyAccount(token));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseTemplate<String>(null, e.getMessage()));
            }
        }
    
    @GetMapping("/email/account")
    public ResponseEntity<ResponseTemplate<?>> resendVerificationEmail(
        @RequestParam String email
    ){
        try {
            return ResponseEntity.ok(new ResponseTemplate<String>(null, verificationService.sendVerificationEmail(email)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseTemplate<String>(null, e.getMessage()));
        }

    }


}
