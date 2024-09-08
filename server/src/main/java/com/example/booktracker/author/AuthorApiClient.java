package com.example.booktracker.author;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class AuthorApiClient {

    private final RestClient restClient;

    @Autowired
    public AuthorApiClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("https://en.wikipedia.org/w/api.php").build();
    }

    public AuthorDTO fetchDetails(String authorName) {
        // get the Wikipedia page title
        ResponseEntity<Map<String, Object>> titleResponse = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("action", "query")
                        .queryParam("list", "search")
                        .queryParam("srsearch", authorName)
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {});

        if (!titleResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to fetch page title for " + authorName);
        }

        Map<String, Object> titleData = titleResponse.getBody();
        List<Map<String, Object>> searchResults = (List<Map<String, Object>>) ((Map<String, Object>) titleData.get("query")).get("search");

        if (searchResults.isEmpty()) {
            return new AuthorDTO("No details found for the author " + authorName, null);
        }

        String pageTitle = (String) searchResults.getFirst().get("title");

        // fetching the introduction and image
        ResponseEntity<Map<String, Object>> extractResponse = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("action", "query")
                        .queryParam("prop", "extracts|pageimages")
                        .queryParam("exintro", "true")
                        .queryParam("explaintext", "true")
                        .queryParam("titles", pageTitle)
                        .queryParam("pithumbsize", "500")
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {});

        if (!extractResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to fetch details for author " + authorName);
        }

        Map<String, Object> extractData = extractResponse.getBody();
        Map<String, Object> pages = (Map<String, Object>) ((Map<String, Object>) extractData.get("query")).get("pages");

        if (pages.isEmpty()) {
            return new AuthorDTO("No pages found for the author " + authorName, null);
        }

        Map<String, Object> pageData = (Map<String, Object>) pages.values().iterator().next();
        String extract = (String) pageData.get("extract");
        String imageUrl = pageData.containsKey("thumbnail") ?
                (String) ((Map<String, Object>) pageData.get("thumbnail")).get("source") : null;

        if (extract.isEmpty() && imageUrl == null) {
            return new AuthorDTO("No details could be found for the author " + authorName, null);
        }

        return new AuthorDTO(extract, imageUrl);
    }
}
