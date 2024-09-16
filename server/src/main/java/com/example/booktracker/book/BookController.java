package com.example.booktracker.book;

import com.example.booktracker.book.customResponses.CacheResponse;
import com.example.booktracker.book.customResponses.SimilarBooksResponse;
import com.example.booktracker.book.exception.BookNotFoundException;
import com.example.booktracker.book.exception.ExternalServiceException;
import com.example.booktracker.book.exception.GenreNotInCacheException;
import com.example.booktracker.book.exception.CustomBadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Endpoint for receiving requests when a user searches for a book.
     *
     * @param search The search term is what the user inputted in the search bar of the application
     * @param limit  The limit is the number provided by the client service for specifying the number of books needed to be returned back to the user
     * @return  A ResponseEntity with the body of type Map<String, List<BookDTO>> that contains a 'books' fields with books as its value
     */
    @GetMapping
    public ResponseEntity<Map<String, List<BookDTO>>> getBooks(@RequestParam(defaultValue = "") String search, @RequestParam(defaultValue = "5") int limit) {

        // error handling for a request with a missing/invalid search or limit parameters
        if (search.isEmpty() || limit <= 0) {
            throw new CustomBadRequestException("Invalid or missing search or limit parameters provided.");
        }

        List<BookDTO> books =  bookService.getBooks(search, limit);

        Map<String, List<BookDTO>> responseObject = new HashMap<>();        // successfull response object
        responseObject.put("books", books);

        return ResponseEntity.ok(responseObject);
    }

    /**
     * Endpoint for receiving requests when default books are being fetched for a genre
     *
     * @param genre The genre which the client is requesting books for
     * @param limit The number of books that should be sent back to the client for the requested genre
     * @return  A ResponseEntity with the body of type Map<String, List<BookDTO>> that contains a 'books' fields with books from the specified genre as its value
     */
    @GetMapping("/{genre}")
    public ResponseEntity<Map<String, List<BookDTO>>> getBooksByGenre(@PathVariable String genre, @RequestParam(defaultValue = "9") int limit) {

        // validating limit if it was entered by client
        if (limit <= 0) {
            throw new CustomBadRequestException("The limit parameter must be a positive integer value.");
        }

        // delegate call to BookService to fetch books for the genre
        List<BookDTO> books = bookService.getBooksByGenre(genre, limit);

        // if books were found
        Map<String, List<BookDTO>> responseObject = new HashMap<>();
        responseObject.put("books", books);

        return ResponseEntity.ok(responseObject);
    }


    /**
     * Endpoint for setting up the server-side in-memory cache with books. This method validates the provided
     * limit parameter to ensure it is a positive integer. It then delegates the cache setup task to the
     * {@link BookService} and returns a {@link CacheResponse} encapsulating the outcome of the cache setup.
     *
     * <p>If the limit parameter is less than or equal to 0, a {@link CustomBadRequestException} is thrown.</p>
     *
     * @param limit The number of books to be stored in each genre within the cache. Default value is 9 if not specified.
     * @return A {@link ResponseEntity} containing a {@link CacheResponse} object with details about the cache setup process.
     * The HTTP status code is set to {@link HttpStatus#OK}.
     * @throws CustomBadRequestException If the provided limit is not a positive integer.
     */
    @GetMapping("/cache")
    public ResponseEntity<CacheResponse> setUpCache(@RequestParam(defaultValue = "9") int limit) {

        // validating limit if it was entered by client
        if (limit <= 0) {
            throw new CustomBadRequestException("The limit parameter must be a positive integer value");
        }

        CacheResponse response = bookService.setUpCache(limit);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Endpoint for retrieving cached books by genre. This method retrieves a list of books from the server-side
     * in-memory cache based on the specified genre and limit. If the genre is not found in the cache or the limit
     * is invalid, an appropriate exception is thrown by the service layer.
     *
     * <p>The response body contains a map with a single key "cachedBooks" which maps to the list of books for the
     * specified genre. If no books are available for the given genre or limit, the list may be empty.</p>
     *
     * @param genre The genre of books to retrieve from the cache.
     * @param limit The maximum number of books to return. Default value is 9 if not specified.
     * @return A {@link ResponseEntity} containing a {@link Map} with the key "cachedBooks" and a list of {@link BookDTO}
     *         objects representing the books retrieved from the cache. The HTTP status code is set to {@link HttpStatus#OK}.
     * @throws GenreNotInCacheException If the specified genre is not present in the cache.
     * @throws CustomBadRequestException If the limit parameter is less than or equal to 0.
     */
    @GetMapping("/cache/{genre}")
    public ResponseEntity<Map<String, List<BookDTO>>> getCachedBooksByGenre(@PathVariable String genre, @RequestParam(defaultValue = "9") int limit) {

        // validating limit if it was entered by client
        if (limit <= 0) {
            throw new CustomBadRequestException("The limit parameter must be a positive integer value.");
        }

        List<BookDTO> cachedBooks = bookService.getCachedBooksByGenre(genre, limit);
        Map<String, List<BookDTO>> cachedBooksResponseMap = new HashMap<>();
        cachedBooksResponseMap.put("cachedBooks", cachedBooks);
        return new ResponseEntity<>(cachedBooksResponseMap, HttpStatus.OK);
    }


    /**
     * Handles the request to update the cache for books of a specific genre.
     * This endpoint updates the cache with new books for the specified genre based on the provided limit.
     * It returns the updated cache for the genre if the update is successful.
     *
     * @param genre The genre for which the cache should be updated. This is a path variable.
     * @param limit The number of books to be fetched and added to the cache for the specified genre. This is a query parameter.
     *
     * @return A `ResponseEntity` containing a map with the key "updatedCache" and a list of `BookDTO` objects representing the updated books for the specified genre.
     *
     * @throws CustomBadRequestException If the limit parameter is invalid (non-positive).
     * @throws GenreNotInCacheException If the specified genre is not present in the cache (handled in the service method).
     * @throws BookNotFoundException If no books are found for the specified genre (handled in the service method).
     * @throws ExternalServiceException If there is an error with the external service (handled in the service method).
     */
    @GetMapping("/cache/{genre}/update")
    public ResponseEntity<Map<String, List<BookDTO>>> updateCachedBooksByGenre(@PathVariable String genre, @RequestParam(defaultValue = "9") int limit) {

        // validating limit if it was entered by client
        if (limit <= 0) {
            throw new CustomBadRequestException("The limit parameter must be a positive integer value.");
        }

        Map<String, List<BookDTO>> responseMap = new HashMap<>();

        List<BookDTO> updatedGenreCache = bookService.updateCachedBooksByGenre(genre, limit);
        responseMap.put("updatedCache", updatedGenreCache);

        return new ResponseEntity<>(responseMap, HttpStatus.OK);

    }

    @GetMapping("/similar")
    public ResponseEntity<SimilarBooksResponse> getSimilarBooks(@RequestParam String title, @RequestParam(defaultValue = "book") String type, @RequestParam(defaultValue = "20") int limit) {

        // validating limit if it was entered by client
        if (limit <= 0) {
            throw new CustomBadRequestException("The limit parameter must be a positive integer value.");
        }

        SimilarBooksResponse similarBooksResult = bookService.getSimilarBooks(title, type, limit);

        return ResponseEntity.ok(similarBooksResult);
    }

}
