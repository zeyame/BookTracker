package com.example.booktracker.user;

public class UserLoginDTO {

    private String username;
    private String password;

    public UserLoginDTO(String username, String password) {
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
