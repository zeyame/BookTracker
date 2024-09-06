package com.example.booktracker.book;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books/{genre}")
    public Flux<BookDTO> getBooksByGenre(@PathVariable String genre) {
        // call BookApiClient method to fetch books for a given genre
        return bookService.getBooksByGenre(genre);
    }

}
