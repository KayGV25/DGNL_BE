package com.dgnl_backend.project.dgnl_backend.exceptions.token;

public class InvalidOTPException extends RuntimeException{
    public InvalidOTPException(String message) {
        super(message);
    }
}
