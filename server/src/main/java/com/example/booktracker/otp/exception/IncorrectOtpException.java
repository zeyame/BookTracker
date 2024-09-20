package com.example.booktracker.otp.exception;

public class IncorrectOtpException extends RuntimeException {
    public IncorrectOtpException(String message) {
        super(message);
    }
}
