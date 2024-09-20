package com.example.booktracker.otp;

import com.example.booktracker.otp.exception.OtpAlreadySentException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private static final Random RANDOM = new Random();

    @Value("${security.jwt.expiration-time}")
    private static long EXPIRATION_TIME;
    @Autowired
    public OtpService(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }
    public String generateOtp() {
        int otp = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(otp);
    }

    public Optional<OtpVerification> findByUsername(String username) {
        return otpRepository.findByUsername(username);
    }

    public Optional<OtpVerification> findActiveOtpByUsername(String username) {
        return otpRepository.findActiveOtpByUsername(username);
    }

    @Transactional
    public void deleteActiveOtpByUsername (String username) {
        otpRepository.deleteActiveOtpByUsername(username);
    }

    @Transactional
    public void save(String otpValue, String username) {

        if (findActiveOtpByUsername(username).isPresent()) {
            throw new OtpAlreadySentException("An OTP has already been sent to the user. Please use the existing OTP or wait before requesting a new one.");
        }

        OtpVerification otp = new OtpVerification();
        otp.setUsername(username);
        otp.setOtp(otpValue);
        otp.setExpirationTime(new Date(System.currentTimeMillis() + EXPIRATION_TIME));

        try {
            otpRepository.save(otp);
        }
        catch (DataIntegrityViolationException exception) {
            throw new OtpAlreadySentException("An OTP has already been sent to the user. Please use the existing OTP or wait before requesting a new one.");
        }

    }
}
