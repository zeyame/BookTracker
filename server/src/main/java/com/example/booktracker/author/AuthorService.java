package com.example.booktracker.author;

import org.springframework.stereotype.Service;

@Service
public class AuthorService {

    private final AuthorApiClient authorApiClient;

    public AuthorService(AuthorApiClient authorApiClient) {
        this.authorApiClient = authorApiClient;
    }

    /**
     * Retrieves the details of an author by calling the fetchDetails method from the API client.
     *
     * @param authorName The name of the author to fetch details for.
     * @return An {@link AuthorDTO} containing the author's description and image URL.
     */
    public AuthorDTO getDetails(String authorName) {
        return authorApiClient.fetchDetails(authorName);
    }
}