package com.example.booktracker.user;

import com.example.booktracker.book.exception.CustomBadRequestException;
import com.example.booktracker.extra_services.EmailService;
import com.example.booktracker.extra_services.JwtService;
import com.example.booktracker.user.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/users")
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
     * Registers a new user with the provided registration details.
     *
     * This method performs the following actions:
     *  Validates that the email, username, and password are provided and non-empty.
     *  Checks if an account with the given email already exists and throws an {@link EmailAlreadyRegisteredException} if so.
     *  Checks if a user with the given username already exists and throws a {@link UsernameAlreadyRegisteredException} if so.
     *  Encodes the provided password and creates a new {@link User} object.
     *  Saves the new user to the database.
     *
     *
     * @param userRegistrationDTO The data transfer object containing user registration details. This includes:
     *                               {@code username} - The desired username for the new user.
     *                               {@code email} - The email address for the new user.
     *                               {@code password} - The plain text password for the new user.
     *
     * @throws CustomBadRequestException if any of the registration fields (username, email, or password) are missing or empty.
     * @throws EmailAlreadyRegisteredException if an account with the provided email already exists.
     * @throws UsernameAlreadyRegisteredException if a user with the provided username already exists.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserRegistrationDTO userRegistrationDTO) {

        Map<String, String> responseMap = new HashMap<>();

        // register new user to database
        userService.register(userRegistrationDTO);

        String username = userRegistrationDTO.getUsername();
        String email = userRegistrationDTO.getEmail();

        // generating a verification token
        String token = jwtService.generateToken(username);

        // verification link
        String link = generateVerificationLink(token, username);

        // sending verification email to registered user
        emailService.sendVerificationEmail(email, "Verify your Shelf Quest account.", "This is your link to verify the account: " + link);

        responseMap.put("message", "User registered successfully. Verification email sent.");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
    }


    /**
     * Handles the email verification process for a user.
     *
     * This endpoint verifies the user's email address by checking the provided
     * verification token and username. If the token is valid and matches the username,
     * the user's verification status is updated to true, allowing them to log in.
     *
     * @param token The verification token sent to the user. It is used to validate
     *              the verification request.
     * @param username The username of the user whose email is being verified.
     *                 This is used to locate the user in the database.
     *
     * @return A {@link ResponseEntity} containing a response map with a message indicating
     *         whether the verification was successful or not. The HTTP status code is set to
     *         {@code 201 Created} to indicate successful processing of the request.
     *
     * @throws InvalidTokenException if the provided token is invalid or does not match
     *                                the expected criteria.
     * @throws InvalidCredentialsException if the user with the provided username
     *                                      cannot be found or any other authentication
     *                                      issues occur.
     */
    @GetMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyUser(@RequestParam String token, @RequestParam String username) {
        // updating user's verification status
        userService.verify(token, username);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "Verification successful. User can now login.");

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
    }

    /**
     * Authenticates a user and generates a JWT token upon successful login.
     *
     * This method handles the login functionality by receiving the user's credentials in the form of
     * a {@link UserLoginDTO}. It validates the credentials by calling the {@code authenticate} method of
     * the {@link UserService}. If authentication is successful, a JWT token is generated using the
     * {@link JwtService} and returned in the response.
     *
     * In case of invalid credentials, an {@link InvalidCredentialsException} is thrown.
     *
     * @param userLoginDTO A {@link UserLoginDTO} object containing the username and password provided by the user.
     * @return A {@link ResponseEntity} containing a success message and a JWT token if authentication succeeds.
     * @throws InvalidCredentialsException If authentication fails due to incorrect username or password.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserLoginDTO userLoginDTO) {
        Map<String, String> responseMap = new HashMap<>();

        String username = userLoginDTO.getUsername();
        String plainPassword = userLoginDTO.getPassword();

        // authenticate user
        userService.authenticate(username, plainPassword);

        // generate token for authenticated user
        String token = jwtService.generateToken(username);
        responseMap.put("message", "User has been authenticated.");
        responseMap.put("token", token);

        return ResponseEntity.ok(responseMap);
    }

    @GetMapping("/verify-token")
    public ResponseEntity<Map<String, String>> verifyToken(String token) {
        // to do
        return null;
    }

    private String generateVerificationLink(String token, String username) {
        return "http://localhost:8080/api/users/verify-email?token="+token+"&username="+username;
    }

}
