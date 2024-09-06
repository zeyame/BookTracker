package com.example.booktracker.book;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

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
        return bookService.getBooksByGenre(genre, limit);
    }

    // endpoint responsible for returning 5 books based on a user's search
    @GetMapping("/books")
    public Flux<BookDTO> getBooks(@RequestParam String search) {
        return bookService.getBooks(search);
    }
}
