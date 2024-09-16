package com.example.booktracker.user.exception;

public class UsernameAlreadyRegisteredException extends RuntimeException {
    public UsernameAlreadyRegisteredException(String message) {
        super(message);
    }
}
