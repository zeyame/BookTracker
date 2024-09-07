package com.example.booktracker.book;


import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BookCache {

    private Map<String, Flux<BookDTO>> cache = new ConcurrentHashMap<>();
    private Map<String, Integer> genreOffset = new HashMap<>();

    public BookCache() {
        initializeGenreOffset();
    }

    public void setUpCache(Map<String, Flux<BookDTO>> cache) {
        this.cache = cache;
    }

    public Map<String, Flux<BookDTO>> getCache() {
        return cache;
    }

    public Map<String, Integer> getGenreOffset() {
        return genreOffset;
    }

    private void initializeGenreOffset() {
        genreOffset.put("romance", 7);
        genreOffset.put("fiction", 7);
        genreOffset.put("thriller", 7);
        genreOffset.put("action", 7);
        genreOffset.put("mystery", 7);
        genreOffset.put("history", 7);
        genreOffset.put("horror", 7);
        genreOffset.put("fantasy", 7);
    }

}
