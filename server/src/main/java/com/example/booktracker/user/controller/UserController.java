package com.example.booktracker.user.controller;

import com.example.booktracker.book.exception.CustomBadRequestException;
import com.example.booktracker.refresh_token.service.RefreshTokenService;
import com.example.booktracker.user.dto.UserDTO;
import com.example.booktracker.utils.CookieUtils;
import com.example.booktracker.extra_services.JwtService;
import com.example.booktracker.user.service.UserService;
import com.example.booktracker.user.exception.*;
import com.example.booktracker.user.request.UserLoginRequest;
import com.example.booktracker.user.request.UserRegistrationRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public UserController(UserService userService, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
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
     * @param request The request object containing user registration details.
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
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserRegistrationRequest request) {

        Map<String, String> responseMap = new HashMap<>();

        // register new user to database
        userService.register(request);

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
     * @param request A {@link UserLoginRequest} object containing the username and password provided by the user.
     * @return A {@link ResponseEntity} containing a success message and a JWT token if authentication succeeds.
     * @throws InvalidCredentialsException If authentication fails due to incorrect username or password.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody @Valid UserLoginRequest request,
                                                         HttpServletResponse response) {
        userService.authenticate(request);

        UserDTO userDTO = userService.getByUsername(request.getUsername());

        // generate jwt and refresh tokens if user has been authenticated
        String jwtToken = jwtService.generateToken(userDTO.getUsername());
        String refreshToken = refreshTokenService.createRefreshToken(userDTO.getId(), userDTO.getUsername());

        // store refresh token in response cookie
        setRefreshTokenCookie(response, refreshToken);

        Map<String, String> body = new HashMap<>();
        body.put("message", "User has been authenticated.");
        body.put("token", jwtToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String oldRefreshToken = CookieUtils.extractRefreshTokenFromCookie(request);
        if (oldRefreshToken == null) {
            throw new InvalidCredentialsException("Refresh token cookie is missing.");
        }

        UserDTO userDTO = userService.getByUsername(jwtService.extractUsername(oldRefreshToken));
        Map<String, String> tokenMap = refreshTokenService.refreshAccessToken(oldRefreshToken, userDTO.getId());

        setRefreshTokenCookie(response, tokenMap.get("refreshToken"));

        // Return the new JWT
        return ResponseEntity.ok(Map.of("token", tokenMap.get("token")));
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refresh_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // enable in prod
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        response.addCookie(cookie);
    }

}
