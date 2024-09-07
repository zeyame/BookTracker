package com.example.booktracker.book;


import com.example.booktracker.GenreNotInCacheException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BookCache {

    private Map<String, List<BookDTO>> cache = new HashMap<>();
    private Map<String, Integer> genreOffset = new HashMap<>();

    public BookCache() {
        initializeGenreOffset();
    }

    public void setUpCache(Map<String, List<BookDTO>> newCache) {
        this.cache = newCache;
    }

    public Map<String, List<BookDTO>> getCache() {
        return cache;
    }

    public Map<String, Integer> getGenreOffset() {
        return genreOffset;
    }

    public List<BookDTO> getCachedBooksByGenre (String genre) throws GenreNotInCacheException{
        List<BookDTO> books = cache.get(genre);
        if (books == null) {
            throw new GenreNotInCacheException(genre + " is not an existing genre in the cache");
        }

        int size = books.size();
        List<BookDTO> booksToReturn = new ArrayList<>();
        if (size > 7) {
            booksToReturn = books.subList(size - 7, size);
            books.subList(size-7, size).clear();
        }
        else {
            booksToReturn = new ArrayList<>(books);
            books.clear();
        }

        return booksToReturn;
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
