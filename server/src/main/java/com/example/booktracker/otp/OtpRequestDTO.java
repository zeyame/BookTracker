package com.example.booktracker.otp;

public class OtpRequestDTO {
    private String email;
    private boolean resend;

    public OtpRequestDTO(String email, boolean resend) {
        this.email = email;
        this.resend = resend;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isResend() {
        return resend;
    }

    public void setResend(boolean resend) {
        this.resend = resend;
    }
}
