package com.example.booktracker.otp.service;

import com.example.booktracker.book.exception.CustomBadRequestException;
import com.example.booktracker.otp.dto.OtpVerificationDTO;
import com.example.booktracker.otp.model.OtpVerification;
import com.example.booktracker.otp.exception.IncorrectOtpException;
import com.example.booktracker.otp.exception.InvalidOtpException;
import com.example.booktracker.otp.exception.OtpAlreadySentException;
import com.example.booktracker.otp.repository.OtpRepository;
import com.example.booktracker.otp.request.VerifyOtpRequest;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    // converts expiration time to a long after dependency injections
    @PostConstruct
    private void init() {
        if (!expirationTimeStr.trim().isEmpty()) {
            try {
                this.EXPIRATION_TIME = Long.parseLong(expirationTimeStr);
            }
            catch (NumberFormatException exception) {
                throw new RuntimeException("Could not parse expiration time to a long.");
            }
        }
    }

    /**
     * Generates a random One Time Password (OTP) consisting of six digits.
     *
     * This method creates an OTP by generating a random integer between 100000 and 999999, ensuring that the OTP is always six digits long.
     *
     * @return a string representation of the generated OTP
     */
    public String generateOtp() {
        int otp = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(otp);
    }


    public boolean emailHasActiveOtp(String email) {
        try {
            return otpRepository.findActiveOtpByEmail(email)
                    .isPresent();
        }
        catch (DataAccessException exception) {
            throw new RuntimeException("An unexpected database error occurred when fetching active otp for user.");
        }
    }

    public List<OtpVerificationDTO> getExpiredOtps() {
        return otpRepository.findExpiredOtps()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Deletes the One Time Password (OTP) associated with the specified username.
     *
     * This method will remove any active OTPs stored in the database for the given username.
     *
     * @param email the username for which the OTP should be deleted
     * @throws CustomBadRequestException if the username is null or empty
     */
    public void deleteOtpByEmail (String email) {
        try {
            otpRepository.deleteOtpByEmail(email);
        }
        catch (DataAccessException exception) {
            throw new RuntimeException("An unexpected database error occurred when deleting otp for user.");
        }
    }

    public void deleteOtps(List<OtpVerificationDTO> otps) {
        for (OtpVerificationDTO otp : otps) {
            otpRepository.deleteOtpByEmail(otp.getEmail());
        }
    }

    /**
     * Saves a new One Time Password (OTP) for the specified user.
     *
     * This method performs validation on the provided username, checks for any currently active OTPs,
     * and if none are found, creates and saves a new OTP along with its expiration time.
     *
     * @param otpValue the OTP value to be saved
     * @param email the username associated with the OTP
     * @throws CustomBadRequestException if the username is null or empty
     * @throws OtpAlreadySentException if an OTP has already been sent to the user
     */
    @Transactional
    public void save(String otpValue, String email) {
        // validating username parameter
        if (email == null || email.trim().isEmpty()) {
            throw new CustomBadRequestException("Email is required to generate the OTP.");
        }

        // ensuring user has no currently active OTPs
        if (otpRepository.findActiveOtpByEmail(email).isPresent()) {
            deleteOtpByEmail(email);
        }

        // creating and saving the OTP
        OtpVerification otp = new OtpVerification();
        otp.setEmail(email);
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

    /**
     * Verifies the provided One Time Password (OTP) for a specified user.
     *
     * This method checks if both the username and OTP are provided, verifies if an active OTP exists
     * for the given username, and checks if the provided OTP matches the stored OTP. If the verification
     * fails, appropriate exceptions are thrown.
     *
     * @param verifyOtpRequestDTO an object containing the OTP and username to be verified
     * @throws CustomBadRequestException if either the username or OTP is null or empty
     * @throws IncorrectOtpException if the provided OTP does not match the stored OTP for the user
     * @throws InvalidOtpException if there is no active OTP for the specified username
     */
    public void verify(VerifyOtpRequest verifyOtpRequestDTO) {
        String email = verifyOtpRequestDTO.getEmail();
        String otp = verifyOtpRequestDTO.getOtp();

        if (Stream.of(email, otp).anyMatch(val -> val == null || val.trim().isEmpty())) {
            throw new CustomBadRequestException("Both email and otp are required for otp validation.");
        }

        Optional<OtpVerification> possibleOtpVerification = otpRepository.findActiveOtpByEmail(email);
        if (possibleOtpVerification.isPresent()) {
            OtpVerification otpVerification = possibleOtpVerification.get();
            String storedOtp = otpVerification.getOtp();

            if (!storedOtp.equals(otp)) {
                throw new IncorrectOtpException("The otp you entered is incorrect.");
            }
        }
        else {
            throw new InvalidOtpException("OTP has expired or invalid. Try requesting a new OTP.");
        }
    }

    private OtpVerificationDTO mapToDTO(OtpVerification otp) {
        return new OtpVerificationDTO(otp.getEmail(), otp.isUsed());
    }
}
