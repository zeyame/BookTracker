package com.example.booktracker.book.customResponses;

import com.example.booktracker.book.BookDTO;

import java.util.List;
import java.util.Map;

public class SimilarBooksResponse {

    private List<BookDTO> similarBooks;
    private Map<String, String> errors;

    public SimilarBooksResponse(Map<String, String> errors, List<BookDTO> similarBooks) {
        this.errors = errors;
        this.similarBooks = similarBooks;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public List<BookDTO> getSimilarBooks() {
        return similarBooks;
    }
}
