package com.dgnl_backend.project.dgnl_backend.exceptions.token;

public class TokenNotFoundException extends RuntimeException{
    public TokenNotFoundException(String message) {
        super(message);
    }
}
