package com.example.booktracker.book;


import com.example.booktracker.GenreNotInCacheException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BookCache {

    private Map<String, List<BookDTO>> cache = new HashMap<>();         // stores the current books in the cache
    private Map<String, Integer> genreOffset = new HashMap<>();         // keeps track of the offset for each genre in the cache
    public BookCache() {
        // Initialize offsets to 0 for all genres
        Arrays.asList("romance", "fiction", "thriller", "action", "mystery", "history", "horror", "fantasy")
                .forEach(genre -> genreOffset.put(genre, 9));
    }

    public void setUpCache(Map<String, List<BookDTO>> newCache) {
        this.cache = newCache;
        newCache.forEach((genre, books) -> genreOffset.put(genre, genreOffset.get(genre) + books.size()));
    }

    public Map<String, List<BookDTO>> getCache() {
        return cache;
    }

    public Map<String, Integer> getGenreOffset() {
        return genreOffset;
    }

    public List<BookDTO> getCachedBooksByGenre(String genre, int limit) throws GenreNotInCacheException {
        List<BookDTO> books = cache.get(genre);
        if (books == null || books.isEmpty()) {
            throw new GenreNotInCacheException(genre + " is not an existing genre in the cache or is empty");
        }

        int booksToReturn = Math.min(limit, books.size());
        List<BookDTO> returnBooks = new ArrayList<>(books.subList(books.size() - booksToReturn, books.size()));

        // Remove the returned books from the cache
        books.subList(books.size() - booksToReturn, books.size()).clear();
        
        return returnBooks;
    }


    public void updateCachedBooksByGenre(String genre, List<BookDTO> newBooks) throws GenreNotInCacheException {
        List<BookDTO> existingBooks = cache.get(genre);
        if (existingBooks == null) {
            throw new GenreNotInCacheException(genre + " is not an existing genre in the cache.");
        }
        existingBooks.addAll(newBooks);
        cache.put(genre, existingBooks);
        updateOffsetForGenre(genre, newBooks.size());
    }

    public int getOffsetForGenre(String genre) throws GenreNotInCacheException {
        Integer offset = genreOffset.get(genre);
        if (offset == null) {
            throw new GenreNotInCacheException(genre + " is not an existing genre in the cache.");
        }
        return offset;
    }

    public List<BookDTO> viewBooksInAGenre(String genre) {
        return cache.get(genre);
    }

    private void updateOffsetForGenre(String genre, int value) {
        genreOffset.put(genre, genreOffset.get(genre) + value);
    }

    private void updateOffsetForAllGenres(int value) {
        for (Map.Entry<String, Integer> entry : genreOffset.entrySet()) {
            String genre = entry.getKey();
            Integer currentOffset = entry.getValue();
            genreOffset.put(genre, currentOffset + value);
        }
    }
}
