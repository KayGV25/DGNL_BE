package com.dgnl_backend.project.dgnl_backend.exceptions.user;

public class PasswordMissMatchException extends RuntimeException {
    public PasswordMissMatchException(String message) {
        super(message);
    }
}
