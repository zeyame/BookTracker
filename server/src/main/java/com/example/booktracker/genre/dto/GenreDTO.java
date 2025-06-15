package com.example.booktracker.genre.dto;

import java.util.List;

public class GenreDTO {
    private final Long id;
    private final String name;

    public GenreDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
