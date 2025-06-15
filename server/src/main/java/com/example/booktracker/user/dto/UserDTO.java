package com.example.booktracker.user.dto;

/**
 * Class represents user data transferred safely between application layers and to the client
 */
public class UserDTO {
    private final Long id;
    private final String username;
    private final String email;

    public UserDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
