package com.example.booktracker.author;

public class AuthorDTO {
    private String description;
    private String imageUrl;

    public AuthorDTO(String description, String imageUrl) {
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
