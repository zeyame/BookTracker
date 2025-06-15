package com.example.booktracker.otp.dto;

public class OtpVerificationDTO {
    private final String email;
    private final boolean used;

    public OtpVerificationDTO(String email, boolean used) {
        this.email = email;
        this.used = used;
    }

    public String getEmail() {
        return email;
    }

    public boolean isUsed() {
        return used;
    }
}
