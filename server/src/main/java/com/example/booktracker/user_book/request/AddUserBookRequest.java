package com.example.booktracker.user_book.request;
import com.example.booktracker.book.dto.BookDTO;
import com.example.booktracker.user_book.ReadingStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class AddUserBookRequest {
    @NotNull(message = "book must not be null")
    @Valid
    private BookDTO bookData;

    @NotNull(message = "status is required")
    private ReadingStatus status;

    private String authorDescription;       // optional

    public BookDTO getBookData() {
        return bookData;
    }

    public ReadingStatus getStatus() {
        return status;
    }

    public String getAuthorDescription() {
        return authorDescription;
    }
}
