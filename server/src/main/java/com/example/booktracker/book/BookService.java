package com.example.booktracker.book;

import com.example.booktracker.GenreNotInCacheException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookApiClient bookApiClient;
    private final BookCache bookCache;

    @Autowired
    public BookService(BookApiClient bookApiClient, BookCache bookCache, ObjectMapper objectMapper) {
        this.bookApiClient = bookApiClient;
        this.bookCache = bookCache;
    }

    // method responsible for requesting books based on a user search from the BookApiClient
    public List<BookDTO> getBooks(String search) {
        return bookApiClient.fetchBooks(search);
    }

    // method is responsible for requesting books for a specific genre from the BookApiClient
    public List<BookDTO> getBooksByGenre(String genre, int limit) {
        return bookApiClient.fetchBooksByGenre(genre, limit);
    }

    // method is responsible for setting up the intial cache in the BookCache repository
    public void setUpCache() {
        HashMap<String, List<BookDTO>> cache = new HashMap<>();
        String[] genres = {"romance", "fiction", "thriller", "action", "mystery", "history", "horror", "fantasy"};

        // Create a map of genre to future, where each future fetches books asynchronously
        Map<String, CompletableFuture<JsonNode>> futureMap = Arrays.stream(genres)
                .collect(Collectors.toMap(
                        genre -> genre,
                        genre -> bookApiClient.fetchBooksByGenreAsync(genre, 7, 7)
                ));

        // Wait for all the futures to complete
        CompletableFuture<Void> jsonResponses = CompletableFuture.allOf(
                futureMap.values().toArray(new CompletableFuture[0])
        );

        // Once all futures complete, process each response and populate the cache
        jsonResponses.thenRun(() -> {
            futureMap.forEach((genre, future) -> {
                try {
                    // Get the completed result from each future
                    JsonNode jsonResponse = future.get();
                    JsonNode bookItems = jsonResponse.get("items");

                    // Check if there are book items and process them
                    if (bookItems != null && bookItems.isArray()) {
                        List<BookDTO> books = new ArrayList<>();
                        for (JsonNode bookItem : bookItems) {
                            books.add(bookApiClient.mapToBookDTO(bookItem));
                        }
                        cache.put(genre, books);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }).join();

        bookCache.setUpCache(cache);

    }

    // method responsible for getting cached books in a specific genre from the BookCache repository
    public List<BookDTO> getCachedBooksByGenre(String genre) throws GenreNotInCacheException {
        List<BookDTO> cachedBooks = bookCache.getCachedBooksByGenre(genre);     // possible exception thrown if genre not in cache
        return cachedBooks;
    }

    public void updateCachedBooksByGenre(String genre, int limit) throws GenreNotInCacheException {
        int currentOffset = bookCache.getOffsetForGenre(genre);

        List<BookDTO> newBooksToCache = bookApiClient.fetchBooksByGenre(genre, limit, currentOffset);
        bookCache.updateCachedBooksByGenre(genre, newBooksToCache);
    }

    // helper method to test cache availability
    public Map<String, List<BookDTO>> getEntireCache() {
        return bookCache.getCache();
    }

}
