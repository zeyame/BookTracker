package com.example.booktracker.book.customResponses;

import com.example.booktracker.book.BookDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheResponse {

    private Map<String, List<BookDTO>> cache = new HashMap<>();
    private Map<String, List<String>> errors = new HashMap<>();

    public CacheResponse (Map<String, List<BookDTO>> cache, Map<String, List<String>> errors) {
        this.cache = cache;
        this.errors = errors;
    }

    public Map<String, List<BookDTO>> getCache() {
        return cache;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }
}
