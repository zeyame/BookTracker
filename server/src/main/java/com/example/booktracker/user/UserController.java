package com.example.booktracker.user;

import com.example.booktracker.EmailService;
import com.example.booktracker.JwtService;
import com.example.booktracker.user.exception.EmailAlreadyRegisteredException;
import com.example.booktracker.user.exception.InvalidTokenException;
import com.example.booktracker.user.exception.UsernameAlreadyRegisteredException;
import com.example.booktracker.user.exception.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserService userService, EmailService emailService, JwtService jwtService) {
        this.userService = userService;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserRegistrationDTO userRegistrationDTO) {

        String username = userRegistrationDTO.getUsername();
        String email = userRegistrationDTO.getEmail();
        String password = userRegistrationDTO.getPassword();


        // handle the case where an account already exists for this email
        if (userService.findByEmail(email).isPresent()) {
            throw new EmailAlreadyRegisteredException("An account with this email is already registered.");
        }

        // handle the case where username already taken
        if (userService.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyRegisteredException("This username already exists.");
        }

        Map<String, String> responseMap = new HashMap<>();

        // save user to database
        User user = new User(username, email, password);
        userService.save(user);

        // generating a verification token
        String token = jwtService.generateToken(user);

        // verification link
        String link = "http://localhost:8080/api/user/verify?token="+token+"&username="+username;

        // sending verification email to registered user
        emailService.sendVerificationEmail(
                email,
                "This is your verification link to complete your registration with Book Tracker",
                "This is your verification link to complete your registration with Book Tracker: " + link
                );

        responseMap.put("message", "User registered successfully. Verification email sent.");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyUser(@RequestParam String token, @RequestParam String username) {
        if (!jwtService.isTokenValid(token, username)) {
            throw new InvalidTokenException("Verification failed. Invalid token.");
        }

        // updating user's verification status
        Optional<User> possibleUser = userService.findByUsername(username);
        if (possibleUser.isPresent()) {
            User user = possibleUser.get();
            user.setIsVerified(true);
            userService.save(user);
        }
        else {
            throw new UsernameNotFoundException("Verification failed. User could not be found");
        }

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "Verification successful. User can now login.");

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserLoginDTO userLoginDTO) {
        // to do
        return null;
    }


}
