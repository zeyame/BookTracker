package com.example.booktracker.user.request;

/**
 * Class represents the object received from the client upon user's attempt to login
 */
public class UserLoginRequest {

    private String username;
    private String password;

    public UserLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
