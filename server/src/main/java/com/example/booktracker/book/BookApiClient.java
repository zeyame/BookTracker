package com.example.booktracker.book;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class BookApiClient {
    private final String GOOGLE_KEY = "AIzaSyBeNJgzSObopgk16PTMPShYOLGwDNt24Ec";
    private final RestClient restClient;
    @Autowired
    public BookApiClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("https://www.googleapis.com/books/v1").build();
    }

    public List<BookDTO> fetchBooksByGenre(String genre, int limit) {
        JsonNode response = restClient.get()
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

    public CompletableFuture<JsonNode> fetchBooksByGenreAsync(String genre, int limit, int offset) {
        return CompletableFuture.supplyAsync(() ->
                restClient.get()
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
        JsonNode response = restClient.get()
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
