package com.example.booktracker.user_book.service;

import com.example.booktracker.book.dto.BookDTO;
import com.example.booktracker.book.exception.BookNotFoundException;
import com.example.booktracker.book.exception.CustomBadRequestException;
import com.example.booktracker.book.service.BookService;
import com.example.booktracker.user.dto.UserDTO;
import com.example.booktracker.user.model.User;
import com.example.booktracker.user.service.UserService;
import com.example.booktracker.user_book.ReadingStatus;
import com.example.booktracker.user_book.model.UserBook;
import com.example.booktracker.user_book.repository.UserBookRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserBookService {

    private final UserBookRepository userBookRepository;
    private final UserService userService;
    private final BookService bookService;

    @Autowired
    public UserBookService(UserBookRepository userBookRepository, UserService userService, BookService bookService) {
        this.userBookRepository = userBookRepository;
        this.userService = userService;
        this.bookService = bookService;
    }

    public UserDTO getUserByUsername(String username) {
        return userService.getByUsername(username);        // may throw a UserNotFoundException
    }

    public Optional<String> getUserBookStatus(String username, String bookId) {
        UserDTO userDTO = userService.getByUsername(username);
        return userBookRepository.findByUserIdAndBookId(userDTO.getId(), bookId)
                .map(userBook -> userBook.getStatus().name());
    }

    public List<BookDTO> getUserBooksByIdAndStatus(Long userId, ReadingStatus status) {
        List<UserBook> userBooks = userBookRepository.findByUserIdAndStatus(userId, status.name());
        List<BookDTO> bookDTOS = new ArrayList<>();
        for (UserBook userBook : userBooks) {
            try {
                bookDTOS.add(bookService.getById(userBook.getBookId()));
            } catch (BookNotFoundException e) {
                // if book does not exist, ignore
            }
        }
        return bookDTOS;
    }

    @Transactional
    public boolean addBookToReadingList(String username, BookDTO bookDTO, ReadingStatus status, String authorDescription) {
        if (username == null || username.isEmpty()) {
            throw new CustomBadRequestException("To add a book to a user's reading list, a valid username is required.");
        }

        if (bookDTO == null) {
            throw new CustomBadRequestException("To add a book to a user's reading, valid book data is required.");
        }

        if (status == null) {
            throw new CustomBadRequestException("To add a book to a user's reading list, a valid status is required:" +
                    " TO_READ, CURRENTLY_READING, READ");
        }

        // get user
        UserDTO userDTO = userService.getByUsername(username);

        // ensure book is persisted
        bookService.findOrCreateBook(bookDTO, authorDescription);

        // check if UserBook entry already exists
        boolean exists = userBookRepository.existsByUserIdAndBookId(userDTO.getId(), bookDTO.getId());
        if (exists) return false;       // will not create a new resource

        // create new UserBook entry
        userBookRepository.insertIgnoreConflict(userDTO.getId(), bookDTO.getId(), status.name());
        return true;
    }

    public boolean updateUserBookStatus(String username, String bookId, ReadingStatus status) {
        if (username == null || username.isEmpty()) {
            throw new CustomBadRequestException("To update the book status for a user, a valid username is required.");
        }

        if (bookId == null || bookId.isEmpty()) {
            throw new CustomBadRequestException("To update the book status for a user, a valid book id is required.");
        }

        if (status == null) {
            throw new CustomBadRequestException("To update the book status for a user, a valid status is required:" +
                    " TO_READ, CURRENTLY_READING, READ");
        }

        // get user
        UserDTO userDTO = userService.getByUsername(username);

        // attempt to update UserBook entry to new status
        int rowsUpdated = userBookRepository.updateStatus(userDTO.getId(), bookId, status.name());
        return rowsUpdated > 0;
    }

    public boolean deleteUserBook(String username, String bookId) {
        if (username == null || username.isEmpty()) {
            throw new CustomBadRequestException("To delete a book from a user's reading list, a valid username is required.");
        }

        if (bookId == null || bookId.isEmpty()) {
            throw new CustomBadRequestException("To delete a book from a user's reading list, a valid book id is required.");
        }

        // get user
        UserDTO userDTO = userService.getByUsername(username);

        int rowsDeleted = userBookRepository.deleteByUserIdAndBookId(userDTO.getId(), bookId);
        return rowsDeleted > 0;
    }
}