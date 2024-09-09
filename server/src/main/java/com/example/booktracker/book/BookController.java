package com.example.booktracker.book;

import com.example.booktracker.GenreNotInCacheException;
import com.example.booktracker.book.exception.CustomBadRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Endpoint is responsible for receiving requests when a user searches for a book.
     *
     * @param search The search term is what the user inputted in the search bar of the application
     * @param limit  The limit is the number provided by the client service for specifying the number of books needed to be returned back to the user
     * @return  A ResponseEntity with the body of type Map<String, List<BookDTO>> that contains a 'books' fields with books as its value
     */
    @GetMapping("/books")
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
     * Endpoint is responsible for receiving requests when default books are being fetched for a genre
     *
     * @param genre The genre which the client is requesting books for
     * @param limit The number of books that should be sent back to the client for the requested genre
     * @return  A ResponseEntity with the body of type Map<String, List<BookDTO>> that contains a 'books' fields with books from the specified genre as its value
     */
    @GetMapping("/books/{genre}")
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

     // endpoint responsible for setting up an in-memory cache for default books in each genre
    @GetMapping("/books/cache")
    public ResponseEntity<Map<String, String>> setUpCache(@RequestParam int limit) {
        bookService.setUpCache(limit);

        Map<String, String> response = new HashMap<>();
        response.put("message", "cache successfully set up.");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // endpoint responsible for retrieving books in a specific genre from the in-memory cache
    @GetMapping("/books/cache/{genre}")
    public ResponseEntity<?> getCachedBooksByGenre(@PathVariable String genre, @RequestParam int limit) throws GenreNotInCacheException {
        try {
            List<BookDTO> cachedBooks = bookService.getCachedBooksByGenre(genre, limit);
            Map<String, List<BookDTO>> cachedBooksResponseMap = new HashMap<>();
            cachedBooksResponseMap.put("cachedBooks", cachedBooks);
            return new ResponseEntity<>(cachedBooksResponseMap, HttpStatus.OK);
        }
        catch (GenreNotInCacheException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());

            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/books/cache/{genre}/update")
    public ResponseEntity<?> updateCachedBooksByGenre(@PathVariable String genre, @RequestParam int limit) throws GenreNotInCacheException {
        Map<String, String> responseMap = new HashMap<>();
        try {
            bookService.updateCachedBooksByGenre(genre, limit);
            responseMap.put("message", genre + " genre has been successfully updated in the cache");

            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        }
        catch (GenreNotInCacheException e) {
            responseMap.put("error", genre + " is not an existing genre in the cache.");
            return new ResponseEntity<>(responseMap, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/books/similar")
    public ResponseEntity<?> getSimilarBooks(@RequestParam String title, @RequestParam String type, @RequestParam int limit) {
        List<BookDTO> similarBooks = bookService.getSimilarBooks(title, type, limit);
        Map<String, List<BookDTO>> response = new HashMap<>();
        response.put("similarBooks", similarBooks);
        return ResponseEntity.ok(response);
    }

    // testing endpoint to check out the state of the server cache
    @GetMapping("/books/cache/full")
    public ResponseEntity<Map<String, List<BookDTO>>> getAllCache() {
        Map<String, List<BookDTO>> cache =  bookService.getEntireCache();
        return new ResponseEntity<>(cache, HttpStatus.OK);
    }

    // testing endpoint to check out the state of a genre in the server cache
    @GetMapping("/books/cache/{genre}/viewing")
    public ResponseEntity<List<BookDTO>> viewBooksInAGenre(@PathVariable String genre) {
        List<BookDTO> books = bookService.viewBooksInAGenre(genre);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }


}
