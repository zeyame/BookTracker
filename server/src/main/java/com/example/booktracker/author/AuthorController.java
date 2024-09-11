package com.example.booktracker.author;

import com.example.booktracker.book.exception.CustomBadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthorController {

    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    /**
     * REST endpoint to retrieve the details of an author by name.
     * This method validates the input and returns the author details as a JSON response.
     *
     * @param authorName The name of the author provided as a request parameter.
     * @return A {@link ResponseEntity} containing a map with the key "authorDetails" and the corresponding {@link AuthorDTO}.
     * @throws CustomBadRequestException If the authorName parameter is empty or invalid.
     */
    @GetMapping("/authors")
    public ResponseEntity<Map<String, AuthorDTO>> getDetails(@RequestParam(defaultValue = "") String authorName) {

        // validating authorName parameter
        if (authorName.trim().isEmpty()) {
            throw new CustomBadRequestException("Author name parameter is required.");
        }

        Map<String, AuthorDTO> responseMap = new HashMap<>();

        AuthorDTO authorDTO = authorService.getDetails(authorName);
        responseMap.put("authorDetails", authorDTO);

        return ResponseEntity.ok(responseMap);
    }
}