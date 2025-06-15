package com.example.booktracker.author.service;

import com.example.booktracker.author.dto.AuthorDTO;
import com.example.booktracker.author.dto.AuthorWikiDTO;
import com.example.booktracker.author.exception.AuthorNotFoundException;
import com.example.booktracker.author.external.AuthorApiClient;
import com.example.booktracker.author.model.Author;
import com.example.booktracker.author.repository.AuthorRepository;
import com.example.booktracker.book.exception.CustomBadRequestException;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorApiClient authorApiClient;

    public AuthorService(AuthorRepository authorRepository, AuthorApiClient authorApiClient) {
        this.authorRepository = authorRepository;
        this.authorApiClient = authorApiClient;
    }

    public AuthorDTO createAuthor(String authorName, String description) {
        if (authorName == null || authorName.isEmpty()) {
            throw new CustomBadRequestException("Author name cannot be null or empty.");
        }
        Optional<Author> existingAuthor = authorRepository.findByName(authorName);
        return existingAuthor.map(this::mapToAuthorDTO)
                .orElseGet(() -> mapToAuthorDTO(authorRepository.save(new Author(authorName, description))));
    }

    public List<AuthorDTO> findOrCreateAuthors(List<String> authorNames, @Nullable String firstAuthorDescription) {
        List<AuthorDTO> authorDTOS = new ArrayList<>();
        for (int i = 0; i < authorNames.size(); i++) {
            String authorName = authorNames.get(i);
            boolean firstAuthor = (i == 0);
            authorDTOS.add(createAuthor(authorName, firstAuthor ? firstAuthorDescription : ""));
        }
        return authorDTOS;
    }

    public AuthorDTO getAuthorByName(String authorName) {
        return authorRepository.findByName(authorName)
                .map(this::mapToAuthorDTO)
                .orElseThrow(() -> new AuthorNotFoundException("Author with this name '" + authorName + "' does not exist."));
    }

    /**
     * Retrieves the details of an author by calling the fetchDetails method from the API client.
     *
     * @param authorName The name of the author to fetch details for.
     * @return An {@link AuthorWikiDTO} containing the author's description and image URL.
     */
    public AuthorWikiDTO getDetails(String authorName) {
        return authorApiClient.fetchDetails(authorName);
    }

    public List<Long> findAuthorsNotInUserReadingLists() {
        return authorRepository.findAuthorsNotInUserReadingLists();
    }

    public void deleteById(Long authorId) {
        authorRepository.deleteById(authorId);
    }

    private AuthorDTO mapToAuthorDTO(Author author) {
        return new AuthorDTO(author.getId(), author.getName(), author.getDescription());
    }

}