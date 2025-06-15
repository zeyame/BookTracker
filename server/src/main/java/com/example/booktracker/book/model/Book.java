package com.example.booktracker.book.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "book")
public class Book {

    @Id
    private String id;

    private String title;
    private List<String> authorNames;
    private String publisher;
    private String description;
    private int pageCount;
    private List<String> genres;
    private String imageUrl;
    private String language;
    List<Long> userBookIds;       // references to entries in UserBook which have the same bookId

    public Book() {}

    public Book(String id, String title, List<String> authorNames, String publisher, String description, int pageCount, List<String> genres, String imageUrl, String language) {
        this.id = id;
        this.title = title;
        this.authorNames = authorNames;
        this.publisher = publisher;
        this.description = description;
        this.pageCount = pageCount;
        this.genres = genres;
        this.imageUrl = imageUrl;
        this.language = language;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<String > getAuthors() {
        return authorNames;
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

    public List<String > getGenres() {
        return genres;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLanguage() {
        return language;
    }

    public List<Long> getUserBookIds() {
        return userBookIds;
    }
}
