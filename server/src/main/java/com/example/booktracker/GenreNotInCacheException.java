package com.example.booktracker;

public class GenreNotInCacheException extends Exception{

    public GenreNotInCacheException(String message) {
        super(message);
    }
}
