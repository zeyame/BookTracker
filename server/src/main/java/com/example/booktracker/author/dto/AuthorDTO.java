package com.example.booktracker.author.dto;

public class AuthorDTO {
    private final Long id;
    private final String name;
    private final String description;

    public AuthorDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
