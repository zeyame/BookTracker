package com.example.booktracker.otp;

import com.example.booktracker.book.exception.CustomBadRequestException;
import com.example.booktracker.extra_services.EmailService;
import com.example.booktracker.user.User;
import com.example.booktracker.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;
    private final EmailService emailService;
    private final UserService userService;

    @Autowired
    public OtpController(OtpService otpService, EmailService emailService, UserService userService) {
        this.otpService = otpService;
        this.emailService = emailService;
        this.userService = userService;
    }

    @Transactional
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody OtpRequest otpRequest) {
        String username = otpRequest.getUsername();
        String email = otpRequest.getEmail();

        if (Stream.of(username, email).anyMatch(val -> val == null || val.trim().isEmpty())) {
            throw new CustomBadRequestException("Both email and username are required to provide an OTP.");
        }

        if (userService.findByUsername(username).isEmpty()) {
            throw new UsernameNotFoundException("An OTP could not be sent as user does not exist.");
        }

        Map<String, String> responseMap = new HashMap<>();

        // Check if an active OTP already exists
        Optional<OtpVerification> existingOtp = otpService.findActiveOtpByUsername(username);
        if (existingOtp.isPresent()) {
            responseMap.put("message", "An OTP has already been sent to the user. Please use the existing OTP or wait before requesting a new one.");
            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        }

        // Generate and send the OTP
        String otp = otpService.generateOtp();

        try {
            // Save OTP to database
            otpService.save(otp, username);

            emailService.sendVerificationEmail(email, "Verify your ShelfQuest email.",
                    "To verify your email address, please use the following One Time Password (OTP):\n\n" + otp);

            responseMap.put("message", "OTP has successfully been sent to the user.");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
        }
        catch (DataIntegrityViolationException exception) {
            responseMap.put("message", "An OTP has already been sent to the user. Please use the existing OTP or wait before requesting a new one.");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
        }
    }
}
