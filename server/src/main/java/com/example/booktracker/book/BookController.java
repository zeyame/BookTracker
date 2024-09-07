package com.example.booktracker.book;

import com.example.booktracker.GenreNotInCacheException;
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

    // endpoint responsible for returning 5 books based on a user's search
    @GetMapping("/books")
    public List<BookDTO> getBooks(@RequestParam String search) {
        return bookService.getBooks(search);
    }

    // endpoint responsible for returning books from a requested genre
    @GetMapping("/books/{genre}")
    public List<BookDTO> getBooksByGenre(@PathVariable String genre, @RequestParam int limit) {
        // call BookApiClient method to fetch books for a given genre
        return bookService.getBooksByGenre(genre, limit);
    }

     // endpoint responsible for setting up an in-memory cache for default books in each genre
    @GetMapping("/books/cache")
    public ResponseEntity<Map<String, String>> setUpCache(@RequestParam int limit) {
        bookService.setUpCache(limit);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Cache set up successfuly");

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
