package com.example.booktracker.book;

import java.util.List;
import java.util.UUID;

public class BookDTO {

    private final String id;
    private final String title;
    private final List<String> authors;
    private final String publisher;
    private final String description;
    private final int pageCount;
    private final List<String> categories;
    private final String imageUrl;
    private final String language;

    public BookDTO(String id, String title, List<String> authors, String publisher, String description, int pageCount, List<String> categories, String imageUrl, String language) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.description = description;
        this.pageCount = pageCount;
        this.categories = categories;
        this.imageUrl = imageUrl;
        this.language = language;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getDescription() {
        return description;
    }

    public int getPageCount() {
        return pageCount;
    }

    public List<String> getCategories() {
        return categories;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLanguage() {
        return language;
    }
}
