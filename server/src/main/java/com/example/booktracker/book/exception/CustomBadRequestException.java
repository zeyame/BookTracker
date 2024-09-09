package com.example.booktracker.book.exception;

public class CustomBadRequestException extends RuntimeException {

    public CustomBadRequestException(String message) {
        super(message);
    }

}
