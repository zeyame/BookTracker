package com.example.booktracker.otp;

public class OtpRequestDTO {
    private String email;
    private String username;

    public OtpRequestDTO(String email, String username) {
        this.email = email;
        this.username = username;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }
}
