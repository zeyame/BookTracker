package com.example.booktracker.author;

import org.springframework.stereotype.Service;

@Service
public class AuthorService {

    private final AuthorApiClient authorApiClient;

    public AuthorService(AuthorApiClient authorApiClient) {
        this.authorApiClient = authorApiClient;
    }

    public AuthorDTO getDetails(String authorName) {
        return authorApiClient.fetchDetails(authorName);
    }
}