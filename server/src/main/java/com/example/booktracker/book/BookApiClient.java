package com.example.booktracker.book;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
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


    public List<BookDTO> fetchBooksByGenre(String genre, int limit) {

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

        if (response == null || !response.has("items")) {
            return Collections.emptyList();
        }

        List<BookDTO> books = new ArrayList<>();
        for (JsonNode bookItem: response.get("items")) {
            books.add(mapToBookDTO(bookItem));
        }

        return books;
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

    
    public List<BookDTO> fetchBooks(String search) {
        JsonNode response = googleBooksClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/volumes")
                        .queryParam("q", search)
                        .queryParam("maxResults", 5)
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
