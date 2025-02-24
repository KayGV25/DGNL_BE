package com.dgnl_backend.project.dgnl_backend.exceptions.token;

public class SendingOTPException extends RuntimeException {
    public SendingOTPException(String message) {
        super(message);
    }
}
