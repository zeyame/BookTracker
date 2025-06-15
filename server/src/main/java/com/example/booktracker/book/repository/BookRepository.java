package com.example.booktracker.book.repository;

import com.example.booktracker.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    @Query(value = """
            SELECT id FROM book 
            WHERE id NOT IN (SELECT DISTINCT book_id FROM user_book)
     """, nativeQuery = true)
    List<String> findBooksNotInUserReadingLists();
}
