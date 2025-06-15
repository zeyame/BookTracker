package com.example.booktracker.user.controller;

import com.example.booktracker.book.exception.CustomBadRequestException;
import com.example.booktracker.extra_services.JwtService;
import com.example.booktracker.user.service.UserService;
import com.example.booktracker.user.exception.*;
import com.example.booktracker.user.request.UserLoginRequest;
import com.example.booktracker.user.request.UserRegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * Validates the user registration details provided in the request body.
     *
     * This endpoint accepts a {@link UserRegistrationRequest} object containing user data (such as
     * username and email), validates it, and returns a success message if the data is valid.
     * The validation process is delegated to the {@link UserService#validate(UserRegistrationRequest)} method.
     *
     * If the validation is successful, a response with a success message is returned.
     * If the validation fails, appropriate exceptions (e.g. {@link CustomBadRequestException},
     * {@link EmailAlreadyRegisteredException}, {@link UsernameAlreadyRegisteredException}) are thrown.
     *
     * @param userRegistrationDTO the {@link UserRegistrationRequest} object containing the user's
     *                            registration details to be validated
     * @return a {@link ResponseEntity} containing a message in a {@link Map}, indicating
     *         that the user data is valid and can be used for registration
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, String>> validateUser(@RequestBody UserRegistrationRequest userRegistrationDTO) {
        // validating user details
        userService.validate(userRegistrationDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "User data is valid and can be used to register a new account."));
    }


    /**
     * Registers a new user and sends a verification email to the provided email address.
     *
     * This endpoint handles the registration process by saving the user's details to the database.
     * After registration, it generates a verification token and constructs a verification link,
     * which is then sent to the user's email address. The user is expected to click the link to
     * verify their email address and complete the registration process.
     *
     * @param userRegistrationRequest The request object containing user registration details.
     *                             It includes the username, email, and password of the user.
     *
     * @return A {@link ResponseEntity} containing a response map with a message indicating
     *         that the user has been successfully registered and that a verification email
     *         has been sent. The HTTP status code is set to {@code 201 Created} to indicate
     *         successful processing of the request.
     *
     * @throws EmailAlreadyRegisteredException if an account with the provided email address
     *                                         already exists.
     * @throws UsernameAlreadyRegisteredException if the provided username is already taken.
     * @throws CustomBadRequestException if any of the required registration fields (email,
     *                                    username, password) are missing or empty.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserRegistrationRequest userRegistrationRequest) {

        Map<String, String> responseMap = new HashMap<>();

        // register new user to database
        userService.register(userRegistrationRequest);

        responseMap.put("message", "User registered successfully.");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
    }

    /**
     * Authenticates a user and generates a JWT token upon successful login.
     *
     * This method handles the login functionality by receiving the user's credentials in the form of
     * a {@link UserLoginRequest}. It validates the credentials by calling the {@code authenticate} method of
     * the {@link UserService}. If authentication is successful, a JWT token is generated using the
     * {@link JwtService} and returned in the response.
     *
     * In case of invalid credentials, an {@link InvalidCredentialsException} is thrown.
     *
     * @param userLoginRequest A {@link UserLoginRequest} object containing the username and password provided by the user.
     * @return A {@link ResponseEntity} containing a success message and a JWT token if authentication succeeds.
     * @throws InvalidCredentialsException If authentication fails due to incorrect username or password.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserLoginRequest userLoginRequest) {
        // authenticate user
        userService.authenticate(userLoginRequest);

        // generate token for authenticated user
        String token = jwtService.generateToken(userLoginRequest.getUsername());

        Map<String, String> responseMap = new HashMap<>();

        responseMap.put("message", "User has been authenticated.");
        responseMap.put("token", token);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
    }
}
