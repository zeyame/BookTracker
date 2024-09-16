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
import org.springframework.mail.MailException;
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


    /**
     * Registers a new user in the system and sends a verification email.
     *
     * <p>This endpoint handles user registration by creating a new user record in the database and sending a verification email
     * with a verification link. The user must confirm their email address by clicking the link in the email to complete the registration.</p>
     *
     * The registration process involves the following steps:
     *   Check if the email provided in the request is already registered. If so, an {@link EmailAlreadyRegisteredException}
     *   is thrown.
     *   Check if the username provided in the request is already taken. If so, a {@link UsernameAlreadyRegisteredException}
     *   is thrown.
     *   Create a new {@link User} object and save it to the database.
     *   Generate a verification token using the {@link JwtService}.
     *   Construct a verification link and send it to the user via email using the {@link EmailService}.
     *   Retry sending the email up to a maximum number of attempts in case of failure.
     *
     * @param userRegistrationDTO The DTO containing user registration details including username, email, and password.
     * @return A {@link ResponseEntity} containing a map with a success message if registration is successful.
     *         The HTTP status code is set to {@link HttpStatus#CREATED}.
     * @throws EmailAlreadyRegisteredException If the email provided is already associated with an existing account.
     * @throws UsernameAlreadyRegisteredException If the username provided is already taken.
     * @throws MailException If an error occurs while sending the verification email. This could occur multiple times
     *         if email sending fails, but will be thrown after a maximum number of retries.
     */
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
        int maxTries = 2;
        int attempt = 0;
        boolean emailSent = false;

        // attempting to send email three times in case of unexpected failure
        while (attempt <= maxTries && !emailSent) {
            try {
                emailService.sendVerificationEmail(
                    email,
                    "Verify Your BookTracker Account",
                    "This is your verification link to complete your registration with Book Tracker: " + link
                );
                emailSent = true;
            }
            catch (MailException e) {
                attempt++;
                // throw exception - handled globally
                if (attempt > maxTries) {
                    throw e;
                }
            }
        }

        responseMap.put("message", "User registered successfully. Verification email sent.");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
    }


    /**
     * Verifies a user's registration using a token and username.
     *
     * This endpoint handles the verification process for a user by validating the token received from the verification link.
     * If the token is valid, it updates the user's verification status in the database. If the token is invalid or the user
     * is not found, appropriate exceptions are thrown and the user is removed from the database if the token is invalid.
     *
     * The verification process involves the following steps:
     *   Validate the provided token using {@link JwtService}. If the token is invalid, remove the user from the database
     *   and throw an {@link InvalidTokenException}.
     *   Check if the user with the provided username exists in the database. If so, update the user's verification status
     *   to true and save the updated user record.
     *   If the user is not found, throw a {@link UsernameNotFoundException}.
     *
     * @param token The verification token sent in the verification link.
     * @param username The username of the user being verified.
     * @return A {@link ResponseEntity} containing a map with a success message if the verification is successful.
     *         The HTTP status code is set to {@link HttpStatus#CREATED}.
     * @throws InvalidTokenException If the token is invalid, the user is removed from the database, and this exception is thrown.
     * @throws UsernameNotFoundException If the user with the provided username is not found in the database.
     */
    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyUser(@RequestParam String token, @RequestParam String username) {
        // validating token sent from verification link
        if (!jwtService.isTokenValid(token, username)) {
            // remove user from db
            userService.deleteByUsername(username);
            throw new InvalidTokenException("Verification failed due to invalid token. Try registering again.");
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
