package com.example.booktracker.book;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // endpoint responsible for returning books from a requested genre
    @GetMapping("/books/{genre}")
    public Flux<BookDTO> getBooksByGenre(@PathVariable String genre, @RequestParam int limit) {
        // call BookApiClient method to fetch books for a given genre
        return bookService.getBooksByGenre(genre, limit);
    }

    // endpoint responsible for returning 5 books based on a user's search
    @GetMapping("/books")
    public Flux<BookDTO> getBooks(@RequestParam String search) {
        return bookService.getBooks(search);
    }

    // endpoint responsible for setting up an in-memory cache for default books in each genre
    @GetMapping("/books/cache")
    public Mono<String> setUpCache() {
        return bookService.setUpCache();
    }
}
