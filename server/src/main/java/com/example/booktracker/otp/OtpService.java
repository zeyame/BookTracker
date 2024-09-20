package com.example.booktracker.otp;

import com.example.booktracker.book.exception.CustomBadRequestException;
import com.example.booktracker.otp.exception.IncorrectOtpException;
import com.example.booktracker.otp.exception.InvalidOtpException;
import com.example.booktracker.otp.exception.OtpAlreadySentException;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Stream;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private static final Random RANDOM = new Random();

    @Value("${security.jwt.expiration-time}")
    private String expirationTimeStr;

    private Long EXPIRATION_TIME;

    @Autowired
    public OtpService(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @PostConstruct
    public void init() {
        if (!expirationTimeStr.trim().isEmpty()) {
            try {
                this.EXPIRATION_TIME = Long.parseLong(expirationTimeStr);
            }
            catch (NumberFormatException exception) {
                throw new RuntimeException("Could not parse expiration time to a long.");
            }
        }
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
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
        otp.setExpirationTime(expirationDate);

        try {
            otpRepository.save(otp);
        }
        catch (DataIntegrityViolationException exception) {
            throw new OtpAlreadySentException("An OTP has already been sent to the user. Please use the existing OTP or wait before requesting a new one.");
        }

    }

    public void verify(VerifyOtpRequestDTO verifyOtpRequestDTO) {
        String otp = verifyOtpRequestDTO.getOtp();
        String username = verifyOtpRequestDTO.getUsername();

        if (Stream.of(otp, username).anyMatch(val -> val == null || val.trim().isEmpty())) {
            throw new CustomBadRequestException("Both username and otp are required for otp validation.");
        }

        Optional<OtpVerification> possibleOtpVerification = findActiveOtpByUsername(username);
        if (possibleOtpVerification.isPresent()) {
            OtpVerification otpVerification = possibleOtpVerification.get();
            String storedOtp = otpVerification.getOtp();

            if (!storedOtp.equals(otp)) {
                throw new IncorrectOtpException("Incorrect otp entered by user.");
            }
        }
        else {
            throw new InvalidOtpException("User has no active otp. Try requesting a new one.");
        }
    }
}
