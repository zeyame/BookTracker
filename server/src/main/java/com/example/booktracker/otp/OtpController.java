package com.example.booktracker.otp;

import com.example.booktracker.book.exception.CustomBadRequestException;
import com.example.booktracker.extra_services.EmailService;
import com.example.booktracker.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
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
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody OtpRequestDTO otpRequest) {
        String username = otpRequest.getUsername();
        String email = otpRequest.getEmail();

        if (Stream.of(username, email).anyMatch(val -> val == null || val.trim().isEmpty())) {
            throw new CustomBadRequestException("Both email and username are required to provide an OTP.");
        }

        if (userService.findByUsername(username).isEmpty()) {
            throw new UsernameNotFoundException("An OTP could not be sent as user is not yet registered.");
        }

        // Generate and send the OTP
        String otp = otpService.generateOtp();

        // save otp with username
        otpService.save(otp, username);

        // send email with otp
        emailService.sendVerificationEmail(email, "Verify your ShelfQuest email.",
                "To verify your email address, please use the following One Time Password (OTP):\n\n" + otp);


        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "OTP has successfully been sent to the user.");

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
    }

    @Transactional
    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestBody VerifyOtpRequestDTO verifyOtpRequestDTO) {
        // verify otp is active and correct for user
        otpService.verify(verifyOtpRequestDTO);

        // delete the otp once validated
        otpService.deleteActiveOtpByUsername(verifyOtpRequestDTO.getUsername());

        // update user's verification status in the users table
        userService.verify(verifyOtpRequestDTO.getUsername());

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "User is now verified and can login. ");

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
    }
}
