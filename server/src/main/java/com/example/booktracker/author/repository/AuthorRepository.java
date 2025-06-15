package com.example.booktracker.author.repository;

import com.example.booktracker.author.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByName(String authorName);

    @Query(value = """
        SELECT id FROM author 
        WHERE id NOT IN (SELECT DISTINCT author_id FROM book_author)      
    """, nativeQuery = true)
    List<Long> findAuthorsNotInUserReadingLists();
}
