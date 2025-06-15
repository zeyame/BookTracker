package com.example.booktracker.author.dto;

public class AuthorWikiDTO {
    private String description;
    private String imageUrl;

    public AuthorWikiDTO(String description, String imageUrl) {
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
