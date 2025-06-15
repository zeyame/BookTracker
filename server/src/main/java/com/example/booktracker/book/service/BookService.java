package com.example.booktracker.book.service;

import com.example.booktracker.author.dto.AuthorDTO;
import com.example.booktracker.author.service.AuthorService;
import com.example.booktracker.book.cache.BookCache;
import com.example.booktracker.book.customResponses.CacheResponse;
import com.example.booktracker.book.customResponses.SimilarBooksResponse;
import com.example.booktracker.book.dto.BookDTO;
import com.example.booktracker.book.exception.GenreNotInCacheException;
import com.example.booktracker.book.exception.BookNotFoundException;
import com.example.booktracker.book.exception.CustomAuthenticationException;
import com.example.booktracker.book.exception.CustomBadRequestException;
import com.example.booktracker.book.exception.ExternalServiceException;
import com.example.booktracker.book.external.BookApiClient;
import com.example.booktracker.book.mapper.BookMapper;
import com.example.booktracker.book.model.Book;
import com.example.booktracker.book.repository.BookAuthorRepository;
import com.example.booktracker.book.repository.BookGenreRepository;
import com.example.booktracker.book.repository.BookRepository;
import com.example.booktracker.genre.dto.GenreDTO;
import com.example.booktracker.genre.service.GenreService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final AuthorService authorService;
    private final GenreService genreService;
    private final BookRepository bookRepo;
    private final BookAuthorRepository bookAuthorRepo;
    private final BookGenreRepository bookGenreRepo;
    private final BookApiClient bookApiClient;
    private final BookCache bookCache;
    private final BookMapper bookMapper;

    @Autowired
    public BookService(AuthorService authorService, GenreService genreService,
                       BookRepository bookRepo, BookAuthorRepository bookAuthorRepo,
                       BookGenreRepository bookGenreRepo, BookApiClient bookApiClient,
                       BookCache bookCache, BookMapper bookMapper) {

        this.authorService = authorService;
        this.genreService = genreService;
        this.bookRepo = bookRepo;
        this.bookAuthorRepo = bookAuthorRepo;
        this.bookGenreRepo = bookGenreRepo;
        this.bookApiClient = bookApiClient;
        this.bookCache = bookCache;
        this.bookMapper = bookMapper;
    }

    @Transactional
    public BookDTO createBook(BookDTO bookDTO, String authorDescription) {
        // find book if already existing or save and get
        Book book = bookRepo.findById(bookDTO.getId())
                .orElseGet(() -> bookRepo.save(bookMapper.fromDTO(bookDTO)));

        // ensure book authors and genres are persisted in the database
        List<AuthorDTO> authorDTOS = authorService.findOrCreateAuthors(bookDTO.getAuthors(), authorDescription);
        List<GenreDTO> genreDTOS = genreService.findOrCreateGenres(bookDTO.getCategories());

        // create BookAuthor entities in join table
        for (AuthorDTO authorDTO : authorDTOS) {
            bookAuthorRepo.insertIgnoreConflict(bookDTO.getId(), authorDTO.getId());
        }

        // create BookGenre entities in join table
        for (GenreDTO genreDTO : genreDTOS) {
            bookGenreRepo.insertIgnoreConflict(bookDTO.getId(), genreDTO.getId());
        }

        // return the created/stored book entity as DTO
        return bookMapper.mapToBookDTO(book);
    }

    public BookDTO findOrCreateBook(BookDTO bookDTO, String authorDescription) {
        return bookRepo.findById(bookDTO.getId())
                .map(bookMapper::mapToBookDTO)
                .orElseGet(() -> createBook(bookDTO, authorDescription));
    }

    public BookDTO getById(String bookId) {
        if (bookId == null || bookId.isEmpty()) {
            throw new CustomBadRequestException("Book ID cannot be null or empty.");
        }
        return bookRepo.findById(bookId)
                .map(bookMapper::mapToBookDTO)
                .orElseThrow(() -> new BookNotFoundException("Book with ID '" + bookId + "' does not exist."));
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

    public List<String> findBooksNotInUserReadingLists() {
        return bookRepo.findBooksNotInUserReadingLists();
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

        String[] genres = {"nonfiction", "fiction", "thriller", "action", "mystery", "history", "horror", "fantasy"};          // genres to fetch books for

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
                            Optional<BookDTO> bookDTOOptional = bookMapper.mapToBookDTO(bookItem);
                            bookDTOOptional.ifPresent(books::add);
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

    public void deleteById(String bookId) {
        bookRepo.deleteById(bookId);
    }
}
