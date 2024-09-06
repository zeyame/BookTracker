package com.example.booktracker.book;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class BookApiClient {
    private final String GOOGLE_KEY = "AIzaSyBeNJgzSObopgk16PTMPShYOLGwDNt24Ec";
    private final WebClient webClient;
    @Autowired
    public BookApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://www.googleapis.com/books/v1").build();
    }

    public Flux<BookDTO> fetchBooksByGenre(String genre, int limit) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/volumes")
                        .queryParam("q", "subject:" + genre)
                        .queryParam("maxResults", limit)
                        .queryParam("fields", "items(id,volumeInfo/title,volumeInfo/authors,volumeInfo/publisher,volumeInfo/publishedDate,volumeInfo/description,volumeInfo/pageCount,volumeInfo/categories,volumeInfo/imageLinks/thumbnail,volumeInfo/language)")
                        .queryParam("key", GOOGLE_KEY)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMapMany(this::parseBookItems);
    }

    private Flux<BookDTO> parseBookItems(JsonNode response) {
        JsonNode items = response.get("items");
        return Flux.fromIterable(() -> items.elements())
                .map(this::convertToBookDto);
    }

    private BookDTO convertToBookDto(JsonNode bookItem) {
        JsonNode volumeInfo = bookItem.get("volumeInfo");

        return new BookDTO(
                UUID.randomUUID(),
                getTextOrEmpty(volumeInfo, "title"),
                getAuthors(volumeInfo.get("authors")),
                getTextOrEmpty(volumeInfo, "publisher"),
                getTextOrEmpty(volumeInfo, "description"),
                volumeInfo.get("pageCount").asInt(0),
                getCategories(volumeInfo.get("categories")),
                getImageUrl(volumeInfo),
                getTextOrEmpty(volumeInfo, "language")
        );
    }

    private List<String> getAuthors(JsonNode authorsNode) {
        if (authorsNode != null && authorsNode.isArray()) {
            List<String> authors = new ArrayList<>();
            for (JsonNode author: authorsNode) {
                authors.add(author.asText());
            }

            return authors;
        }
        return Collections.emptyList();
    }

    private List<String> getCategories(JsonNode categoriesNode) {
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

}
