package com.example.booktracker.book;

import com.example.booktracker.book.customResponses.CacheResponse;
import com.example.booktracker.book.customResponses.SimilarBooksResponse;
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

    /**
     * Retrieves a list of books from the cache based on the specified genre and limit. This method delegates the
     * request to the {@link BookCache} to fetch books for the given genre. If the genre is not present in the cache
     * or if an error occurs during retrieval, an appropriate exception will be thrown by the {@link BookCache}.
     *
     * @param genre The genre of books to retrieve from the cache. Must be a valid genre that exists in the cache.
     * @param limit The maximum number of books to return. If the limit exceeds the number of books available for
     *              the genre, only the available books will be returned.
     * @return A {@link List} of {@link BookDTO} objects representing the books retrieved from the cache.
     * @throws GenreNotInCacheException If the specified genre is not present in the cache.
     */
    public List<BookDTO> getCachedBooksByGenre(String genre, int limit) {
        return bookCache.getCachedBooksByGenre(genre, limit);     // possible exception thrown if genre not in cache
    }


    /**
     * Updates the cache with new books for the specified genre.
     * This method fetches books from the external API based on the given genre and limit, then updates the cache.
     * It returns the updated list of books for the genre.
     *
     * @param genre The genre for which the cache should be updated.
     * @param limit The number of books to be fetched and added to the cache.
     *
     * @return A list of `BookDTO` objects representing the new books added to the cache for the specified genre.
     *
     * @throws GenreNotInCacheException If the specified genre is not present in the cache.
     * @throws BookNotFoundException If no books are found for the specified genre.
     * @throws ExternalServiceException If there is an error with the external service.
     */
    public List<BookDTO> updateCachedBooksByGenre(String genre, int limit) {
        int currentOffset = bookCache.getOffsetForGenre(genre);

        List<BookDTO> newBooksToCache = bookApiClient.fetchBooksByGenre(genre, limit, currentOffset);
        return bookCache.updateCachedBooksByGenre(genre, newBooksToCache);
    }


    /**
     * Fetches similar books for the provided title by calling an external API.
     * This method uses the type and limit parameters to filter the results.
     *
     * @param title the title of the book to find similar books for
     * @param type  the type of media (default is "book")
     * @param limit the maximum number of similar books to retrieve
     * @return a {@link SimilarBooksResponse} containing similar book data and errors
     */
    public SimilarBooksResponse getSimilarBooks(String title, String type, int limit) {
        return bookApiClient.fetchSimilarBooks(title, type, limit);
    }

}
