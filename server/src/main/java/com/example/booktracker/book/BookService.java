package com.example.booktracker.book;

import com.example.booktracker.book.exception.GenreNotInCacheException;
import com.example.booktracker.book.exception.BookNotFoundException;
import com.example.booktracker.book.exception.CustomAuthenticationException;
import com.example.booktracker.book.exception.CustomBadRequestException;
import com.example.booktracker.book.exception.ExternalServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
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

    /**
     * Acts as an intermediary method for the GET /api/books endpoint
     * Delegates the request to fetch a specific number of books from an external API to the BookApiClient
     * It receives the fetched books and sends them back to the controller
     *
     * @param search The search term provided by the GET /api/books endpoint
     * @param limit  The limit term provided by the GET /api/books endpoint
     * @return A list of {@link BookDTO} objects representing the books that match the search term.
     *
     * @throws CustomBadRequestException If the search term is empty or the limit is non-positive.
     * @throws CustomAuthenticationException If there is an error with the API key.
     * @throws BookNotFoundException If no books are found for the search term.
     * @throws ExternalServiceException If there is an error with the external service or something unexpected happened.
     */
    public List<BookDTO> getBooks(String search, int limit) {
        return bookApiClient.fetchBooks(search, limit);
    }


    /**
     * Acts as an intermediary method for the GET /api/books/{genre} endpoint
     * Delegates the request to fetch a specific number of books from an external API to the BookApiClient
     * It receives the fetched books and sends them back to the controller
     *
     * @param genre The genre term provided by the GET /api/books/{genre} endpoint
     * @param limit  The limit term provided by the GET /api/books/{genre} endpoint
     * @return A list of {@link BookDTO} objects representing the books retrieved for the requested genre.
     *
     * @throws CustomBadRequestException If the genre or limit parameters are invalid or missing.
     * @throws CustomAuthenticationException If there is an error with the API key.
     * @throws BookNotFoundException If no books are found for the genre term.
     * @throws ExternalServiceException If there is an error with the external service or something unexpected happened.
     */
    public List<BookDTO> getBooksByGenre(String genre, int limit) {
        return bookApiClient.fetchBooksByGenre(genre, limit);
    }


    /**
     * Acts as an intermediary method for the GET /api/books/cache endpoint
     * Delegates the request to fetch a specific number of books from an external API to the BookApiClient
     * It receives the fetched books and sends them back to the controller
     *
     * @param limit  The limit term provided by the GET /api/books/{genre} endpoint
     *
     * @throws CustomBadRequestException If the genre or limit parameters are invalid or missing.
     * @throws CustomAuthenticationException If there is an error with the API key.
     * @throws BookNotFoundException If no books are found for a genre term.
     * @throws ExternalServiceException If there is an error with the external service or something unexpected happened.
     */
    public CacheResponse setUpCache(int limit) {
        Map<String, List<BookDTO>> cache = new HashMap<>();     // initial cache to be populated

        Map<String, List<String>> errors = new HashMap<>();     // potential errors when fetching books for different genres
        errors.put("errors", new ArrayList<>());

        String[] genres = {"romance", "fiction", "thriller", "action", "mystery", "history", "horror", "fantasy"};          // genres to fetch books for

        // Create a map of genre to future, where each future fetches books asynchronously
        Map<String, CompletableFuture<JsonNode>> futureMap = Arrays.stream(genres)
                .collect(Collectors.toMap(
                        genre -> genre,
                        genre -> bookApiClient.fetchBooksByGenreAsync(genre, limit,  limit)
                                .exceptionally(ex -> {
                                    errors.get("errors").add(ex.getMessage());
                                    return null;
                                })
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

                    if (jsonResponse == null) {
                        System.out.println("Skipping cache populate for genre: " + genre + ".");
                        return;
                    }

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
                    errors.get("errors").add("Unexpected error occurred when setting up cache. " + e.getMessage());
                }
            });
        }).join();

        bookCache.setUpCache(cache);

        return new CacheResponse(cache, errors);

    }

    // method responsible for getting cached books in a specific genre from the BookCache repository
    public List<BookDTO> getCachedBooksByGenre(String genre, int limit) throws GenreNotInCacheException {
        return bookCache.getCachedBooksByGenre(genre, limit);     // possible exception thrown if genre not in cache
    }

    public void updateCachedBooksByGenre(String genre, int limit) throws GenreNotInCacheException {
        int currentOffset = bookCache.getOffsetForGenre(genre);

        List<BookDTO> newBooksToCache = bookApiClient.fetchBooksByGenre(genre, limit, currentOffset);
        bookCache.updateCachedBooksByGenre(genre, newBooksToCache);
    }

    public List<BookDTO> getSimilarBooks(String title, String type, int limit) {
        return bookApiClient.fetchSimilarBooks(title, type, limit);
    }

    // helper method to test cache availability
    public Map<String, List<BookDTO>> getEntireCache() {
        return bookCache.getCache();
    }

    public List<BookDTO> viewBooksInAGenre(String genre) {
        return bookCache.viewBooksInAGenre(genre);
    }

}
