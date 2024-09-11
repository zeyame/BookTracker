package com.example.booktracker.book;

import java.util.List;
import java.util.Map;

public class SimilarBooksResponse {

    private List<BookDTO> similarBooks;
    private Map<String, List<String>> errors;

    public SimilarBooksResponse(Map<String, List<String>> errors, List<BookDTO> similarBooks) {
        this.errors = errors;
        this.similarBooks = similarBooks;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }

    public List<BookDTO> getSimilarBooks() {
        return similarBooks;
    }
}
