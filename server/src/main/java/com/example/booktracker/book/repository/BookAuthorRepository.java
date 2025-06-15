package com.example.booktracker.book.repository;

import com.example.booktracker.book.model.BookAuthorLink;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookAuthorRepository extends JpaRepository<BookAuthorLink, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO book_author (book_id, author_id)
            VALUES (:bookId, :authorId)
            ON CONFLICT (book_id, author_id) DO NOTHING
     """, nativeQuery = true)
    void insertIgnoreConflict(@Param("bookId") String bookId, @Param("authorId") Long authorId);
}
