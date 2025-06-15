package com.example.booktracker.book.model;

import jakarta.persistence.*;

@Entity
@Table(name = "book_author")
public class BookAuthorLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookId;
    private Long authorId;

    public BookAuthorLink() {}

    public BookAuthorLink(String bookId, Long authorId) {
        this.bookId = bookId;
        this.authorId = authorId;
    }

    public Long getId() {
        return id;
    }

    public String getBookId() {
        return bookId;
    }

    public Long getAuthorId() {
        return authorId;
    }
}
