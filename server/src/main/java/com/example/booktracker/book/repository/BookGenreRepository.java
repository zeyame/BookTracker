package com.example.booktracker.book.repository;

import com.example.booktracker.genre.model.Genre;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookGenreRepository extends JpaRepository<Genre, Long> {
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO book_genre (book_id, genre_id)
        VALUES (:bookId, :genreId) 
        ON CONFLICT (book_id, genre_id) DO NOTHING        
    """, nativeQuery = true)
    void insertIgnoreConflict(@Param("bookId") String bookId, @Param("genreId") Long genreId);
}
