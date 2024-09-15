package com.example.booktracker.user;

import com.example.booktracker.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    @Autowired
    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/user/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserDTO userDTO) {

        String username = userDTO.getUsername();
        String email = userDTO.getEmail();
        String password = userDTO.getPassword();

        Map<String, String> responseMap = new HashMap<>();

        if (userService.findByEmail(email) != null) {
            // handle the case where an account already exists for this email
            responseMap.put("message", "An account with this email is already registered.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseMap);
        }

        if (userService.findByUsername(username) != null) {
            // handle the case where username already taken
            responseMap.put("message", "This username already exists.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseMap);
        }

        // save user to database
        User user = new User(username, email, password);
        userService.save(user);

        // verifying user email
        emailService.sendVerificationEmail(email, "Verify your email to use BookTracker.", "This is the verification link.");

        responseMap.put("message", "verification email sent.");

        return ResponseEntity.ok(responseMap);
    }
}
