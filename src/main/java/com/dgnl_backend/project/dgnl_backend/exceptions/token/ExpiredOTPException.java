package com.dgnl_backend.project.dgnl_backend.exceptions.token;

public class ExpiredOTPException extends RuntimeException {
    public ExpiredOTPException(String message) {
        super(message);
    }
}
