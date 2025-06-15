package com.example.booktracker.user_book.request;

import com.example.booktracker.user_book.ReadingStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateUserBookRequest {
    @NotNull
    private String bookId;

    @NotNull
    private ReadingStatus status;

    public UpdateUserBookRequest() {}

    public UpdateUserBookRequest(String bookId, ReadingStatus status) {
        this.bookId = bookId;
        this.status = status;
    }

    public String getBookId() {
        return bookId;
    }

    public ReadingStatus getStatus() {
        return status;
    }
}
