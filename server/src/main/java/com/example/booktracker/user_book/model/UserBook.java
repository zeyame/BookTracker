package com.example.booktracker.user_book.model;

import com.example.booktracker.user_book.ReadingStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "user_book")
public class UserBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String bookId;

    @Enumerated(EnumType.STRING)
    private ReadingStatus status;

    public UserBook() {}

    public UserBook(Long userId, String bookId, ReadingStatus status) {
        this.userId = userId;
        this.bookId = bookId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getBookId() {
        return bookId;
    }

    public ReadingStatus getStatus() {
        return status;
    }

}
