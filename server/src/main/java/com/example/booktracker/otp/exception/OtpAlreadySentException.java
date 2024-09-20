package com.example.booktracker.otp.exception;

public class OtpAlreadySentException extends RuntimeException {

    public OtpAlreadySentException(String message) {
        super(message);
    }
}
