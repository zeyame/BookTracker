package com.example.booktracker.otp;

public class OtpRequestDTO {
    private String email;

    public OtpRequestDTO() {

    }

    public OtpRequestDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
