package com.dgnl_backend.project.dgnl_backend.exceptions.user;

public class EmailInvalidException extends RuntimeException {
    public EmailInvalidException(String message) {
        super(message);
    }
}
