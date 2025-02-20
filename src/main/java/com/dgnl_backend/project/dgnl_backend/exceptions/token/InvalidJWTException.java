package com.dgnl_backend.project.dgnl_backend.exceptions.token;

public class InvalidJWTException extends RuntimeException {
    public InvalidJWTException(String message) {
        super(message);
    }
}
