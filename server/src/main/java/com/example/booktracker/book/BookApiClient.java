package com.example.booktracker.book;


import com.example.booktracker.book.exception.BookNotFoundException;
import com.example.booktracker.book.exception.CustomAuthenticationException;
import com.example.booktracker.book.exception.CustomBadRequestException;
import com.example.booktracker.book.exception.ExternalServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import javax.naming.AuthenticationException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class BookApiClient {
    private static final String TASTEDIVE_BASE_URL = "https://tastedive.com/api";
    private static final String GOOGLE_BOOKS_BASE_URL = "https://www.googleapis.com/books/v1";

    private final String TASTEDIVE_KEY = "1033070-BookTrac-59A622F3";
    private final String GOOGLE_KEY = "AIzaSyD7tk0i-j5aVlJtsyTuZeGI5Y--C-9AWJE";

    private final RestClient tasteDiveClient;
    private final RestClient googleBooksClient;

    @Autowired
    public BookApiClient(RestClient.Builder restClientBuilder) {
        this.tasteDiveClient = restClientBuilder
                .baseUrl(TASTEDIVE_BASE_URL)
                .build();

        // Create a new builder for the Google Books client
        this.googleBooksClient = RestClient.builder()
                .baseUrl(GOOGLE_BOOKS_BASE_URL)
                .build();
    }
    public CompletableFuture<BookDTO> fetchBookAsync(String title) {
        return CompletableFuture.supplyAsync(() -> {
            // Make the async API call
            JsonNode response = googleBooksClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/volumes")
                            .queryParam("q", "intitle:" + title)
                            .queryParam("maxResults", 1)
                            .queryParam("fields", "items(id,volumeInfo/title,volumeInfo/authors,volumeInfo/publisher,volumeInfo/publishedDate,volumeInfo/description,volumeInfo/pageCount,volumeInfo/categories,volumeInfo/imageLinks/thumbnail,volumeInfo/language)")
                            .queryParam("key", GOOGLE_KEY)
                            .build())
                    .retrieve()
                    .body(JsonNode.class);

            // Process the response
            if (response == null || !response.has("items")) {
                return null; // Handle no items found
            }

            JsonNode bookItem = response.get("items").get(0);
            return mapToBookDTO(bookItem); // Convert to BookDTO
        });
    }


    /**
     * Fetches a list of books based on the provided search term.
     *
     * This method queries the Google Books API to retrieve a list of books that match the search term. It limits
     * the number of results to the specified limit. If no books are found or an error occurs, appropriate exceptions
     * are thrown.
     *
     * @param search The search term used to query for books. This should be a non-empty string.
     * @param limit The maximum number of results to return. This should be a positive integer.
     * @return A list of {@link BookDTO} objects representing the books that match the search term.
     *
     * @throws CustomBadRequestException If the search term is empty or the limit is non-positive.
     * @throws CustomAuthenticationException If there is an error with the API key.
     * @throws BookNotFoundException If no books are found for the search term.
     * @throws ExternalServiceException If there is an error with the external service or something unexpected happened.
     */
    public List<BookDTO> fetchBooks(String search, Integer limit) {
        // requesting books from Google Books API
        try {
            JsonNode response = googleBooksClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/volumes")
                            .queryParam("q", search)
                            .queryParam("maxResults", limit)
                            .queryParam("fields", "items(id,volumeInfo/title,volumeInfo/authors,volumeInfo/publisher,volumeInfo/publishedDate,volumeInfo/description,volumeInfo/pageCount,volumeInfo/categories,volumeInfo/imageLinks/thumbnail,volumeInfo/language)")
                            .queryParam("key", GOOGLE_KEY)
                            .build())
                    .retrieve()
                    .body(JsonNode.class);

            // checking if the response contains books
            if (response == null || !response.has("items")) {
                throw new BookNotFoundException("No books found for search: " + search + ".");
            }

            List<BookDTO> books = new ArrayList<>();
            // mapping each returned book to a BookDTO
            for (JsonNode bookItem: response.get("items")) {
                books.add(mapToBookDTO(bookItem));
            }

            return books;
        }
        catch (RestClientResponseException e) {
            HttpStatusCode statusCode = e.getStatusCode();

            // handling client side errors
            if (statusCode.is4xxClientError()) {
                switch (statusCode.value()) {
                    case 400:
                        throw new CustomBadRequestException("Bad request when fetching books for search: " + search + ". Check parameters.");

                    case 401:
                    case 403:
                        throw new CustomAuthenticationException("Unauthenticated/unauthorized request when fetching books for search: " + search + ". Check key validity.");

                    case 404:
                        throw new BookNotFoundException("No books found for search: " + search + ".");

                    default:
                        throw new RuntimeException("Client error occurred when fetching books for search: " + search + ".");
                }
            }
            // handling server-side errors
            else if (statusCode.is5xxServerError()) {
                throw new ExternalServiceException("External service error occurred when fetching books for search: " + search + ".");
            }
            else {
                throw new ExternalServiceException("Unexpected error occurred when fetching books for search: " + search + ".");
            }
        }
    }


    /**
     * Fetches a list of books in a given genre.
     *
     * This method queries the Google Books API to retrieve a list of books that are found in the provided the genre term. It limits
     * the number of results to the specified limit. If no books are found or an error occurs, appropriate exceptions
     * are thrown.
     *
     * @param genre The genre term used to query for books. This should be a non-empty string.
     * @param limit The maximum number of books to return. This should be a positive integer.
     * @return A list of {@link BookDTO} objects representing the books that match the search term.
     *
     * @throws CustomBadRequestException If the search term is empty or the limit is non-positive.
     * @throws CustomAuthenticationException If there is an error with the API key.
     * @throws BookNotFoundException If no books are found for the search term.
     * @throws ExternalServiceException If there is an error with the external service or something unexpected happened.
     */
    public List<BookDTO> fetchBooksByGenre(String genre, int limit) {
        // fetching a 'limit' number of books for a specific genre from the Google Books API
        try {
            JsonNode response = googleBooksClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/volumes")
                            .queryParam("q", "subject:" + genre)
                            .queryParam("maxResults", limit)
                            .queryParam("fields", "items(id,volumeInfo/title,volumeInfo/authors,volumeInfo/publisher,volumeInfo/publishedDate,volumeInfo/description,volumeInfo/pageCount,volumeInfo/categories,volumeInfo/imageLinks/thumbnail,volumeInfo/language)")
                            .queryParam("key", GOOGLE_KEY)
                            .build())
                    .retrieve()
                    .body(JsonNode.class);

            // validating if response contains books
            if (response == null || !response.has("items")) {
                throw new BookNotFoundException("No books found for genre: " + genre + ".");
            }

            List<BookDTO> books = new ArrayList<>();
            // mapping the books to BookDTOs
            for (JsonNode bookItem: response.get("items")) {
                books.add(mapToBookDTO(bookItem));
            }

            return books;
        }
        catch (RestClientResponseException exception) {
            HttpStatusCode statusCode = exception.getStatusCode();

            // handling client-side errors
            if (statusCode.is4xxClientError()) {
                switch (statusCode.value()) {

                    case 400:
                        throw new CustomBadRequestException("Bad request when fetching books for the genre: " + genre + ". Check query parameters.");

                    case 401:
                    case 403:
                        throw new CustomAuthenticationException("Unathenticated/unathorized request when fetching books for the genre: " + genre + ". Check validity of API key.");

                    case 404:
                        throw new BookNotFoundException("No books found for genre: " + genre + ".");

                    default:
                        throw new RuntimeException("Client error occurred when fetching books for genre: " + genre + ".");
                }
            }

            // handling server-side errors (external service)
            else if (statusCode.is5xxServerError()) {
                throw new ExternalServiceException("External service error occurred when fetching books for genre: " + genre + ".");
            }
            else {
                throw new ExternalServiceException("Unexpected error occurred when fetching books for genre: " + genre + ".");
            }
        }
    }

    public List<BookDTO> fetchBooksByGenre(String genre, int limit, int offset) {
        JsonNode response = googleBooksClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/volumes")
                        .queryParam("q", "subject:" + genre)
                        .queryParam("maxResults", limit)
                        .queryParam("startIndex", offset)
                        .queryParam("fields", "items(id,volumeInfo/title,volumeInfo/authors,volumeInfo/publisher,volumeInfo/publishedDate,volumeInfo/description,volumeInfo/pageCount,volumeInfo/categories,volumeInfo/imageLinks/thumbnail,volumeInfo/language)")
                        .queryParam("key", GOOGLE_KEY)
                        .build())
                .retrieve()
                .body(JsonNode.class);

        if (response == null || !response.has("items")) {
            return Collections.emptyList();
        }

        List<BookDTO> books = new ArrayList<>();
        for (JsonNode bookItem: response.get("items")) {
            books.add(mapToBookDTO(bookItem));
        }

        return books;
    }

    public CompletableFuture<JsonNode> fetchBooksByGenreAsync(String genre, int limit, int offset) {
        return CompletableFuture.supplyAsync(() ->
                googleBooksClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/volumes")
                                .queryParam("q", "subject:" + genre)
                                .queryParam("maxResults", limit)
                                .queryParam("startIndex", offset)
                                .queryParam("fields", "items(id,volumeInfo/title,volumeInfo/authors,volumeInfo/publisher,volumeInfo/publishedDate,volumeInfo/description,volumeInfo/pageCount,volumeInfo/categories,volumeInfo/imageLinks/thumbnail,volumeInfo/language)")
                                .queryParam("key", GOOGLE_KEY)
                                .build())
                        .retrieve()
                        .body(JsonNode.class)
                );
    }


    public List<BookDTO> fetchSimilarBooks(String title, String type, int limit) {

        String tasteDiveUrl = "https://tastedive.com/api/similar";

        JsonNode response = tasteDiveClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/similar")
                        .queryParam("q", "book:" + title)
                        .queryParam("type", type)
                        .queryParam("limit", limit)
                        .queryParam("k", TASTEDIVE_KEY)
                        .build())
                .retrieve()
                .body(JsonNode.class);

        if (response == null || !response.has("similar")) {
            return null;
        }
        JsonNode similarBooksJson = response.get("similar").get("results");

        List<String> bookNames = new ArrayList<>();
        if (similarBooksJson != null && similarBooksJson.isArray()) {
            for (JsonNode bookItem: similarBooksJson) {
                bookNames.add(getTextOrEmpty(bookItem, "name"));
            }
        }

        // gathering the async calls for all book names
        List<CompletableFuture<BookDTO>> bookDTOCalls = bookNames.stream()
                .map(this::fetchBookAsync)
                .collect(Collectors.toList());

        // executing all the calls asynchronously at once and waiting for them to finish - this is our 'sync' point
        CompletableFuture<Void> allFutureBooks = CompletableFuture.allOf(
                bookDTOCalls.toArray(new CompletableFuture[0])
        );

        // extracting the actual BookDTOs from the bookDTOCalls once allFutureBooks signals 'complete'
        CompletableFuture<List<BookDTO>> allFutures = allFutureBooks.thenApply(v ->
                bookDTOCalls.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
                );

        return allFutures.join();

    }

    public BookDTO mapToBookDTO(JsonNode bookItem) {
        JsonNode volumeInfo = bookItem.get("volumeInfo");

        if (volumeInfo != null) {
            UUID id = UUID.randomUUID();
            String title = getTextOrEmpty(volumeInfo, "title");
            List<String> authors = getAuthors(volumeInfo);
            String publisher = getTextOrEmpty(volumeInfo, "publisher");
            String description = getTextOrEmpty(volumeInfo, "description");
            int pageCount = getPageCount(volumeInfo);
            List<String> categories = getCategories(volumeInfo);
            String imageUrl = getImageUrl(volumeInfo);
            String language = getTextOrEmpty(volumeInfo, "language");

            return new BookDTO(id, title, authors, publisher, description, pageCount, categories, imageUrl, language);
        }

        return null;
    }
    

    private List<String> getAuthors(JsonNode volumeInfo) {
        JsonNode authorsNode = volumeInfo.get("authors");
        if (authorsNode != null && authorsNode.isArray()) {
            List<String> authors = new ArrayList<>();
            for (JsonNode author: authorsNode) {
                authors.add(author.asText());
            }

            return authors;
        }
        return Collections.emptyList();
    }

    private List<String> getCategories(JsonNode volumeInfo) {
        JsonNode categoriesNode = volumeInfo.get("categories");
        if (categoriesNode != null && categoriesNode.isArray()) {
            List<String> categories = new ArrayList<>();
            for (JsonNode category: categoriesNode) {
                categories.add(category.asText());
            }

            return categories;
        }

        return Collections.emptyList();
    }

    private String getImageUrl(JsonNode volumeInfo) {
        JsonNode imageLinks = volumeInfo.get("imageLinks");
        return (imageLinks != null) ? getTextOrEmpty(imageLinks, "thumbnail") : "";
    }

    private String getTextOrEmpty(JsonNode item, String fieldName) {
        JsonNode fieldValue = item.get(fieldName);
        return (fieldValue != null) ? fieldValue.asText() : "";
    }

    private int getPageCount(JsonNode volumeInfo) {
        JsonNode pageCountNode = volumeInfo.get("pageCount");
        return (pageCountNode != null && pageCountNode.isNumber()) ? pageCountNode.asInt(0) : 0;
    }
}
