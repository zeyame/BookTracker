package com.example.booktracker.user.request;

/**
 * Class represents the object received from the client upon user's attempt to register
 */
public class UserRegistrationRequest {

    private String username;
    private String email;
    private String password;

    public UserRegistrationRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
