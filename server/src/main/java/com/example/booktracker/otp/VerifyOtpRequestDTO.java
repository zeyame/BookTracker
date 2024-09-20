package com.example.booktracker.otp;

public class VerifyOtpRequestDTO {

    private String username;
    private String otp;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public VerifyOtpRequestDTO(String username, String otp) {
        this.username = username;
        this.otp = otp;
    }
}
