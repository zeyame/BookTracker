package com.example.booktracker.user_book.controller;

import com.example.booktracker.book.dto.BookDTO;
import com.example.booktracker.user_book.ReadingStatus;
import com.example.booktracker.user_book.request.UpdateUserBookRequest;
import com.example.booktracker.user_book.service.UserBookService;
import com.example.booktracker.user_book.request.AddUserBookRequest;
import jakarta.validation.Valid;
import com.example.booktracker.user.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/books")
public class UserBookController {
    private final UserBookService userBookService;

    @Autowired
    public UserBookController(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    /**
     * Retrieves the list of books in the authenticated user's reading list by the specified status.
     *
     * @param status The reading status (e.g., TO_READ, CURRENTLY_READING, READ)
     * @param authentication Injected by Spring Security; contains the authenticated user
     * @return A list of BookDTOs wrapped in a map with key "books"
     */
    @GetMapping
    public ResponseEntity<Map<String, List<BookDTO>>> getBooksByStatus(@RequestParam ReadingStatus status,
                                                                       Authentication authentication) {
        String username = authentication.getName();
        UserDTO userDTO = userBookService.getUserByUsername(username);
        Long userId = userDTO.getId();
        List<BookDTO> bookDTOS = userBookService.getUserBooksByIdAndStatus(userId, status);

        Map<String, List<BookDTO>> responseMap = new HashMap<>();
        responseMap.put("books", bookDTOS);
        return ResponseEntity.ok(responseMap);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createUserBook(@RequestBody @Valid AddUserBookRequest request,
                                                                    Authentication authentication) {
        String username = authentication.getName();
        boolean created = userBookService.addBookToReadingList(username, request.getBookData(),
                request.getStatus(), request.getAuthorDescription());

        if (created) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("message", "book added successfully to the user's '"
                            + request.getStatus().name() + "' list."));
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("message", "book already exists in one of the user's reading lists."));
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> updateUserBookStatus(@RequestBody @Valid UpdateUserBookRequest request,
                                                                    Authentication authentication) {
        String username = authentication.getName();
        boolean updated = userBookService.updateUserBookStatus(username, request.getBookId(), request.getStatus());

        if (updated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("message", "book status updated successfully"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "book does not exist in any of the user's reading lists."));
        }
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Map<String, String>> deleteUserBook(@PathVariable String bookId,
                                                              Authentication authentication) {
        String username = authentication.getName();
        boolean deleted = userBookService.deleteUserBook(username, bookId);

        if (deleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("message", "book was successfully removed from the user's reading list."));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "book does not exist in any of the user's reading lists."));
        }
    }
}
