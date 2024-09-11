package com.example.booktracker.author;

import com.example.booktracker.author.exception.AuthorNotFoundException;
import com.example.booktracker.book.exception.BookNotFoundException;
import com.example.booktracker.book.exception.CustomAuthenticationException;
import com.example.booktracker.book.exception.CustomBadRequestException;
import com.example.booktracker.book.exception.ExternalServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Service
public class AuthorApiClient {

    private final RestClient restClient;

    @Autowired
    public AuthorApiClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("https://en.wikipedia.org/w/api.php").build();
    }


    /**
     * Fetches details of an author from Wikipedia by performing two separate API calls:
     * 1. To retrieve the Wikipedia page title of the author.
     * 2. To fetch the author's introduction and image using the retrieved page title.
     *
     * @param authorName The name of the author to search for.
     * @return An {@link AuthorDTO} containing the author's description and image URL.
     * @throws AuthorNotFoundException If no results are found for the given author.
     * @throws CustomBadRequestException If there is an issue with the API request parameters.
     * @throws CustomAuthenticationException If the request is unauthorized or unauthenticated.
     * @throws ExternalServiceException If an external service (Wikipedia) returns a server-side error.
     * @throws RuntimeException If an unexpected error occurs during the process.
     */
    public AuthorDTO fetchDetails(String authorName) {
        try {
            // get the Wikipedia page title
            JsonNode titleResponse = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("action", "query")
                            .queryParam("list", "search")
                            .queryParam("srsearch", authorName)
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .body(JsonNode.class);

            if (titleResponse == null || !titleResponse.has("query")) {
                throw new AuthorNotFoundException("No results could be found for the author with the name: " + authorName);
            }

            JsonNode searchResults = titleResponse.get("query").get("search");
            if (searchResults.isEmpty()) {
                throw new AuthorNotFoundException("No results could be found for the author with the name: " + authorName);
            }

            String pageTitle = searchResults.get(0).get("title").asText();

            // fetching the introduction and image
            JsonNode extractResponse = restClient.get()
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
                    .body(JsonNode.class);

            if (extractResponse == null || !extractResponse.has("query") || !extractResponse.get("query").has("pages")) {
                throw new AuthorNotFoundException("No results could be found for the author with the name: " + authorName);
            }

            JsonNode pages = extractResponse.get("query").get("pages");
            JsonNode pageData = pages.elements().next();

            if (pageData == null) {
                throw new AuthorNotFoundException("No page data found for the author with the name: " + authorName);
            }

            String description = pageData.has("extract") ? pageData.get("extract").asText() : "No description available.";

            String imageUrl = null;
            if (pageData.has("thumbnail") && pageData.get("thumbnail").has("source")) {
                imageUrl = pageData.get("thumbnail").get("source").asText();
            }

            return new AuthorDTO(description, imageUrl);
        }
        catch (RestClientResponseException exception) {
            HttpStatusCode statusCode = exception.getStatusCode();
            // handling client-side errors
            if (statusCode.is4xxClientError()) {
                switch (statusCode.value()) {

                    case 400:
                        throw new CustomBadRequestException("Bad request when fetching author details for the author: " + authorName + ". Check query parameters.");

                    case 401:
                    case 403:
                        throw new CustomAuthenticationException("Unathenticated/unathorized request when fetching author details for the author: " + authorName + ". Check validity of API key.");

                    case 404:
                        throw new AuthorNotFoundException("No similar books found to the book: " + authorName + ".");

                    default:
                        throw new RuntimeException("Client error occurred when fetching author details for the author: " + authorName + ".");
                }
            }

            // handling server-side errors (external service)
            else if (statusCode.is5xxServerError()) {
                throw new ExternalServiceException("External service error occurred when fetching author details for the author: " + authorName + ".");
            }
            else {
                throw new ExternalServiceException("Unexpected error occurred when fetching author details for the author: " + authorName + ".");
            }
        }
        catch (Exception e) {
            // Handle other exceptions
            throw new RuntimeException("Unexpected error occurred while fetching author details for the author: " + authorName + ". " + e.getMessage());
        }
    }
}
