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

    public void setUpCache(Map<String, List<BookDTO>> newCache, int offset) {
        this.cache = newCache;
        updateOffsetForAllGenres(offset);
    }

    public Map<String, List<BookDTO>> getCache() {
        return cache;
    }

    public Map<String, Integer> getGenreOffset() {
        return genreOffset;
    }

    public List<BookDTO> getCachedBooksByGenre(String genre, int limit) throws GenreNotInCacheException {
        List<BookDTO> books = cache.get(genre);
        if (books == null) {
            throw new GenreNotInCacheException(genre + " is not an existing genre in the cache");
        }

        int size = books.size();
        List<BookDTO> booksToReturn = new ArrayList<>();

        // Retrieve the last `limit` books if available
        if (size > limit) {
            booksToReturn = new ArrayList<>(books.subList(size - limit, size));
            // Remove the last `limit` books from the cache
            books.subList(size - limit, size).clear();
        } else {
            booksToReturn = new ArrayList<>(books);
            // Clear all books if there are fewer than `limit` books
            books.clear();
        }

        return booksToReturn;
    }


    public void updateCachedBooksByGenre(String genre, List<BookDTO> newBooks) throws GenreNotInCacheException {
        List<BookDTO> currentCachedBooks = cache.get(genre);
        if (currentCachedBooks == null) {
            throw new GenreNotInCacheException(genre + " is not an existing genre in the cache");
        }

        // adding new set of books to the end of current set of cached books
        currentCachedBooks.addAll(newBooks);

        // incrementing the current offset of the genre so that next set of books will be new
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
