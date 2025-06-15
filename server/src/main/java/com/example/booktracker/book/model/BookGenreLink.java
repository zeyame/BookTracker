package com.example.booktracker.book.model;

import jakarta.persistence.*;

@Entity
@Table(name = "book_genre")
public class BookGenreLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookId;
    private Long genreId;

    public BookGenreLink() {}

    public BookGenreLink(String bookId, Long genreId) {
        this.bookId = bookId;
        this.genreId = genreId;
    }

    public Long getId() {
        return id;
    }

    public String getBookId() {
        return bookId;
    }

    public Long getGenreId() {
        return genreId;
    }
}
