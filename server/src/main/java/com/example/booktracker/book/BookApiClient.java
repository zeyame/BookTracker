package com.example.booktracker.book;


import com.example.booktracker.book.customResponses.SimilarBooksResponse;
import com.example.booktracker.book.exception.BookNotFoundException;
import com.example.booktracker.book.exception.CustomAuthenticationException;
import com.example.booktracker.book.exception.CustomBadRequestException;
import com.example.booktracker.book.exception.ExternalServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class BookApiClient {
    private static final String TASTEDIVE_BASE_URL = "https://tastedive.com/api";
    private static final String GOOGLE_BOOKS_BASE_URL = "https://www.googleapis.com/books/v1";

    @Value("${tastedive.api-key}")
    private String TASTEDIVE_KEY;

    @Value("${google.books.api-key}")
    private String GOOGLE_KEY;

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


    public CompletableFuture<BookDTO> fetchBookByTitle(String title) {
        return CompletableFuture.supplyAsync(() -> {
            try {
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
                    throw new BookNotFoundException("No book found with the title: " + title);
                }

                JsonNode bookItem = response.get("items").get(0);
                return mapToBookDTO(bookItem); // Convert to BookDTO

            } catch (RestClientResponseException exception) {
                throw handleApiException(exception, "fetching book with title: " + title);
            } catch (Exception e) {
                // Handle other exceptions
                throw new RuntimeException("Unexpected error occurred while fetching book with title: " + title + ". " + e.getMessage());
            }
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
            throw handleApiException(e, "fetching books for search " + search);
        }
        catch (Exception e) {
            // Handle other exceptions
            throw new RuntimeException("Unexpected error occurred while fetching books for search: " + search + ". " + e.getMessage());
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
     * @throws CustomBadRequestException If the query parameters provided to the API were invalid.
     * @throws CustomAuthenticationException If there is an error with the API key.
     * @throws BookNotFoundException If no books are found for the genre.
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
            throw handleApiException(exception, "fetching books for the genre " + genre);
        }
        catch (Exception e) {
            // Handle other exceptions
            throw new RuntimeException("Unexpected error occurred while fetching books for genre: " + genre + ". " + e.getMessage());
        }
    }

    /**
     * Fetches a list of books in a given genre.
     *
     * This method queries the Google Books API to retrieve a list of books that are found in the provided the genre term. It starts fetching book number offset+1 (0-based indexing).
     * It limits the number of results to the specified limit. If no books are found or an error occurs, appropriate exceptions are thrown.
     *
     * @param genre The genre term used to query for books. This should be a non-empty string.
     * @param limit The maximum number of books to return. This should be a positive integer.
     * @param offset The start index of where to start fetching books from.
     * @return A list of {@link BookDTO} objects representing the books that match the search term.
     *
     * @throws CustomBadRequestException If the query parameters provided to the API were invalid.
     * @throws CustomAuthenticationException If there is an error with the API key.
     * @throws BookNotFoundException If no books are found for the genre.
     * @throws ExternalServiceException If there is an error with the external service or something unexpected happened.
     */
    public List<BookDTO> fetchBooksByGenre(String genre, int limit, int offset) {
        try {
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
                throw new BookNotFoundException("No books found for genre: " + genre + ".");
            }

            List<BookDTO> books = new ArrayList<>();
            for (JsonNode bookItem: response.get("items")) {
                books.add(mapToBookDTO(bookItem));
            }

            return books;
        }
        catch (RestClientResponseException exception) {
            throw handleApiException(exception, "fetching books for the genre " + genre);
        }
        catch (Exception e) {
            // Handle other exceptions
            throw new RuntimeException("Unexpected error occurred while fetching books for genre: " + genre + ". " + e.getMessage());
        }
    }


    /**
     * Fetches a list of books in a given genre asynchronously.
     *
     * This method queries the Google Books API to retrieve a list of books that are found in the provided the genre term. It starts fetching book number offset+1 (0-based indexing).
     * It limits the number of results to the specified limit. If no books are found or an error occurs, appropriate exceptions are thrown.
     *
     * @param genre The genre term used to query for books. This should be a non-empty string.
     * @param limit The maximum number of books to return. This should be a positive integer.
     * @param offset The start index of where to start fetching books from.
     * @return A list of {@link BookDTO} objects representing the books that match the search term.
     *
     * @throws CustomBadRequestException If the query parameters provided to the API were invalid.
     * @throws CustomAuthenticationException If there is an error with the API key.
     * @throws BookNotFoundException If no books are found for the genre.
     * @throws ExternalServiceException If there is an error with the external service or something unexpected happened.
     */
    public CompletableFuture<JsonNode> fetchBooksByGenreAsync(String genre, int limit, int offset) {
        return CompletableFuture.supplyAsync(() -> {
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
                        throw new BookNotFoundException("No books found for genre: " + genre + ".");
                    }

                    return response;
                }).exceptionally(ex -> {

                    if (ex.getCause() instanceof RestClientResponseException restEx) {
                        throw handleApiException(restEx, "fetching books for the genre " + genre);
                    }

                    throw new ExternalServiceException("Unexpected error occurred when fetching books for genre: " + genre + ".");
        });
    }


    /**
     * Fetches similar books from the TasteDive API for a given book title.
     * It handles both client and server errors gracefully and asynchronously fetches the details of each similar book.
     * If a book fetch fails, the error is recorded in the returned response.
     *
     * @param title the title of the book to find similar books for
     * @param type  the type of media (default is "book")
     * @param limit the maximum number of similar books to retrieve
     * @return a {@link SimilarBooksResponse} containing similar book data and a list of errors
     * @throws BookNotFoundException if no similar books are found
     * @throws CustomBadRequestException if the request contains invalid parameters
     * @throws CustomAuthenticationException if there is an issue with authentication (e.g., invalid API key)
     * @throws ExternalServiceException if the external service returns a 5xx error or any other unexpected error occurs
     */
    public SimilarBooksResponse fetchSimilarBooks(String title, String type, int limit) {
        List<String> bookNames = new ArrayList<>();

        try {
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
                throw new BookNotFoundException("No similar books found for the book: " + title + ".");
            }

            JsonNode similarBooksJson = response.get("similar").get("results");

            if (similarBooksJson == null || !similarBooksJson.isArray()) {
                throw new BookNotFoundException("No similar books found for the book: " + title + ".");
            }

            for (JsonNode bookItem: similarBooksJson) {
                bookNames.add(getTextOrEmpty(bookItem, "name"));
            }
        }
        catch (RestClientResponseException exception) {
            throw handleApiException(exception, "fetching similar books to the book: " + title);
        }
        catch (Exception e) {
            // Handle other exceptions
            throw new RuntimeException("Unexpected error occurred while fetching similar books to book: " + title + ". " + e.getMessage());
        }

        // map will contain all possible errors which may be thrown when fetching data of all 20 similar books
        Map<String, String> errorsMap = new HashMap<>();
        AtomicInteger errorCounter = new AtomicInteger(1);

        // gathering the async calls for all book names
        List<CompletableFuture<BookDTO>> bookDTOCalls = bookNames.stream()
                .map(name -> fetchBookByTitle(name)
                        .handle((result, exception) -> {
                            if (exception != null) {
                                errorsMap.put("error " + errorCounter.getAndIncrement(), "Error while fetching data for the similar book with title: " + title);
                                return null;
                            }
                            return result;
                        })
                )
                .toList();

        // executing all the calls asynchronously at once and waiting for them to finish - this is our 'sync' point
        CompletableFuture<Void> allFutureBooks = CompletableFuture.allOf(
                bookDTOCalls.toArray(new CompletableFuture[0])
        );

        // extracting the actual BookDTOs from the bookDTOCalls once allFutureBooks signals 'complete'
        List<BookDTO> similarBooks = allFutureBooks.thenApply(v ->
                bookDTOCalls.stream()
                        .filter(Objects::nonNull)
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
                ).join();

        return new SimilarBooksResponse(errorsMap, similarBooks);
    }

    private RuntimeException handleApiException(RestClientResponseException exception, String context) {
        HttpStatusCode statusCode = exception.getStatusCode();
        if (statusCode.is4xxClientError()) {
            return switch (statusCode.value()) {
                case 400 -> new CustomBadRequestException("Bad request when " + context + ". Check query parameters.");
                case 401, 403 -> new CustomAuthenticationException("Unauthorized request when " + context + ". Check validity of API key.");
                case 404 -> new BookNotFoundException("No books found when " + context + ".");
                default -> new RuntimeException("Client error occurred when " + context + ".");
            };
        } else if (statusCode.is5xxServerError()) {
            return new ExternalServiceException("External service error occurred when " + context + ".");
        } else {
            return new ExternalServiceException("Unexpected error occurred when " + context + ".");
        }
    }


    /**
     * Maps a given {@link JsonNode} representing a book's details to a {@link BookDTO} object.
     * It extracts relevant fields such as title, authors, publisher, description, page count, categories,
     * image URL, and language from the provided JSON data. If the "volumeInfo" node is missing or null,
     * the method returns {@code null}.
     *
     * @param bookItem the {@link JsonNode} representing a book's details
     * @return a {@link BookDTO} containing the mapped data, or {@code null} if no "volumeInfo" is present
     */
    public BookDTO mapToBookDTO(JsonNode bookItem) {
        String bookId = getTextOrEmpty(bookItem, "id");

        JsonNode volumeInfo = bookItem.get("volumeInfo");
        if (volumeInfo != null) {
            String title = getTextOrEmpty(volumeInfo, "title");
            List<String> authors = getAuthors(volumeInfo);
            String publisher = getTextOrEmpty(volumeInfo, "publisher");
            String description = getTextOrEmpty(volumeInfo, "description");
            int pageCount = getPageCount(volumeInfo);
            List<String> categories = getCategories(volumeInfo);
            String imageUrl = getImageUrl(volumeInfo);
            String language = getTextOrEmpty(volumeInfo, "language");

            return new BookDTO(bookId, title, authors, publisher, description, pageCount, categories, imageUrl, language);
        }

        return null;
    }


    /**
     * Retrieves the list of authors from the given {@link JsonNode}.
     * Extracts and returns the "authors" field from the "volumeInfo" node.
     * If no authors are found or the field is not an array, an empty list is returned.
     *
     * @param volumeInfo the {@link JsonNode} representing the book's volume information
     * @return a {@link List} of authors, or an empty list if no authors are found
     */
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


    /**
     * Retrieves the list of categories from the given {@link JsonNode}.
     * Extracts and returns the "categories" field from the "volumeInfo" node.
     * If no categories are found or the field is not an array, an empty list is returned.
     *
     * @param volumeInfo the {@link JsonNode} representing the book's volume information
     * @return a {@link List} of categories, or an empty list if no categories are found
     */
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


    /**
     * Retrieves the thumbnail image URL for the book from the given {@link JsonNode}.
     * Extracts and returns the "thumbnail" field from the "imageLinks" node.
     * If no image link is found, an empty string is returned.
     *
     * @param volumeInfo the {@link JsonNode} representing the book's volume information
     * @return the thumbnail URL as a {@link String}, or an empty string if no image link is found
     */
    private String getImageUrl(JsonNode volumeInfo) {
        JsonNode imageLinks = volumeInfo.get("imageLinks");
        return (imageLinks != null) ? getTextOrEmpty(imageLinks, "thumbnail") : "https://via.placeholder.com/150x220?text=No+Cover+Available";
    }


    /**
     * Retrieves the text value of a specified field from the given {@link JsonNode}.
     * Returns the field's text value if it exists, otherwise returns an empty string.
     *
     * @param item      the {@link JsonNode} from which to extract the field
     * @param fieldName the name of the field to retrieve
     * @return the field's text value as a {@link String}, or an empty string if the field is not found
     */
    private String getTextOrEmpty(JsonNode item, String fieldName) {
        JsonNode fieldValue = item.get(fieldName);
        return (fieldValue != null) ? fieldValue.asText() : "";
    }


    /**
     * Retrieves the page count from the given {@link JsonNode}.
     * Extracts and returns the "pageCount" field as an integer. If the field is not found
     * or is not a number, returns 0 as the default value.
     *
     * @param volumeInfo the {@link JsonNode} representing the book's volume information
     * @return the page count as an {@code int}, or 0 if the field is not found or is not a number
     */
    private int getPageCount(JsonNode volumeInfo) {
        JsonNode pageCountNode = volumeInfo.get("pageCount");
        return (pageCountNode != null && pageCountNode.isNumber()) ? pageCountNode.asInt(0) : 0;
    }
}
