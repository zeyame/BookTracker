package com.example.booktracker.otp;

public class OtpRequestDTO {
    private String email;
    private String username;
    private boolean resend;

    public OtpRequestDTO(String email, String username, boolean resend) {
        this.email = email;
        this.username = username;
        this.resend = resend;
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

    public boolean isResend() {
        return resend;
    }

    public void setResend(boolean resend) {
        this.resend = resend;
    }
}
