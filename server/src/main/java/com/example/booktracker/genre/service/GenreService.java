package com.example.booktracker.genre.service;

import com.example.booktracker.book.exception.CustomBadRequestException;
import com.example.booktracker.genre.dto.GenreDTO;
import com.example.booktracker.genre.model.Genre;
import com.example.booktracker.genre.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public GenreDTO createGenre(String genreName) {
        if (genreName == null || genreName.isEmpty()) {
            throw new CustomBadRequestException("Genre name cannot be null or empty.");
        }
        Optional<Genre> existingGenre = genreRepository.findByName(genreName);
        return existingGenre.map(this::mapToGenreDTO)
                .orElseGet(() -> mapToGenreDTO(genreRepository.save(new Genre(genreName))));
    }

    public List<GenreDTO> findOrCreateGenres(List<String> genreNames) {
        List<GenreDTO> genreDTOS = new ArrayList<>();
        for (String name : genreNames) {
            Optional<Genre> existingGenre = genreRepository.findByName(name);
            if (existingGenre.isEmpty()) {
                GenreDTO createdGenre = createGenre(name);
                genreDTOS.add(createdGenre);
            } else genreDTOS.add(mapToGenreDTO(existingGenre.get()));
        }

        return genreDTOS;
    }

    public List<Long> findGenresNotInUserReadingLists() {
        return genreRepository.findGenresNotInUserReadingLists();
    }

    public void deleteById(Long genreId) {
        genreRepository.deleteById(genreId);
    }

    private GenreDTO mapToGenreDTO(Genre genre) {
        return new GenreDTO(genre.getId(), genre.getName());
    }
}
