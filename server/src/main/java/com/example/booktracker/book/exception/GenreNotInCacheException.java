package com.example.booktracker.book.exception;

public class GenreNotInCacheException extends RuntimeException {

    public GenreNotInCacheException(String message) {
        super(message);
    }
}
