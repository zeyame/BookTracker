package com.example.booktracker.maintenance;

import com.example.booktracker.author.service.AuthorService;
import com.example.booktracker.book.service.BookService;
import com.example.booktracker.genre.service.GenreService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookCleanUpService {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;

    @Autowired
    public BookCleanUpService(BookService bookService, AuthorService authorService, GenreService genreService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.genreService = genreService;
    }

    @Scheduled(fixedRate = 30000) // Every 30s
    @Transactional
    public void cleanUnreferencedBooks() {
        // remove all unreferenced books from the Book table
        List<String> unreferencedBookIds = bookService.findBooksNotInUserReadingLists();
        for (String bookId : unreferencedBookIds) {
            bookService.deleteById(bookId);
        }

        // remove all unreferenced authors from the Author table
        List<Long> unreferencedAuthorIds = authorService.findAuthorsNotInUserReadingLists();
        for (Long authorId : unreferencedAuthorIds) {
            authorService.deleteById(authorId);
        }

        // remove all unreferenced genres from the Genre table
        List<Long> unreferencedGenreIds = genreService.findGenresNotInUserReadingLists();
        for (Long genreId : unreferencedGenreIds) {
            genreService.deleteById(genreId);
        }
    }
}
