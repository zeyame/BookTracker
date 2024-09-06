package com.example.booktracker.book;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class BookService {

    private final BookApiClient bookApiClient;

    public BookService(BookApiClient bookApiClient) {
        this.bookApiClient = bookApiClient;
    }

    public Flux<BookDTO> getBooksByGenre(String genre, int limit) {
        return bookApiClient.fetchBooksByGenre(genre, limit);
    }

    // method responsible for requesting books based on a user search from the BookApiClient
    public Flux<BookDTO> getBooks(String search) {
        return bookApiClient.fetchBooks(search);
    }
}
