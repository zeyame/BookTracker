package com.example.booktracker.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BookService {

    private final BookApiClient bookApiClient;
    private final BookCache bookCache;

    @Autowired
    public BookService(BookApiClient bookApiClient, BookCache bookCache) {
        this.bookApiClient = bookApiClient;
        this.bookCache = bookCache;
    }

    public Flux<BookDTO> getBooksByGenre(String genre, int limit) {
        return bookApiClient.fetchBooksByGenre(genre, limit);
    }

    // method responsible for requesting books based on a user search from the BookApiClient
    public Flux<BookDTO> getBooks(String search) {
        return bookApiClient.fetchBooks(search);
    }

    public Mono<String> setUpCache() {
        String[] genres = {"romance", "fiction", "thriller", "action", "mystery", "history", "horror", "fantasy"};

        return Flux.fromArray(genres)
                .flatMap(genre -> bookApiClient.fetchBooksByGenre(genre, 7, 7)
                        .collectList()
                        .map(books -> Map.entry(genre, books)))
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .doOnNext(cache -> {
                    Map<String, Flux<BookDTO>> fluxCache = new ConcurrentHashMap<>();
                    cache.forEach((genre, books) -> fluxCache.put(genre, Flux.fromIterable(books)));
                    bookCache.setUpCache(fluxCache);
                })
                .thenReturn("Cache set up successfully.");
    }
}
