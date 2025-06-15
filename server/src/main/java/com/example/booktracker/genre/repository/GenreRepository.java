package com.example.booktracker.genre.repository;

import com.example.booktracker.genre.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByName(String genreName);

    @Query(value = """
        SELECT id FROM genre 
        WHERE id NOT IN (SELECT DISTINCT genre_id FROM book_genre)       
     """, nativeQuery = true)
    List<Long> findGenresNotInUserReadingLists();
}
