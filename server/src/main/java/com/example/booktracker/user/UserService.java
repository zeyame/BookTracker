package com.example.booktracker.user;

import com.example.booktracker.book.exception.CustomBadRequestException;
import com.example.booktracker.extra_services.JwtService;
import com.example.booktracker.user.exception.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }


    /**
     * Retrieves a user by their username.
     *
     * This method attempts to find a user in the database by their username. If an error occurs during the database access,
     * a {@link RuntimeException} is thrown with a descriptive error message.
     *
     * @param userName The username of the user to be retrieved.
     * @return An {@link Optional} containing the user if found, or an empty {@link Optional} if no user with the given username exists.
     * @throws RuntimeException If an error occurs when accessing the database.
     */
    public Optional<User> findByUsername(String userName) {
        try {
            return userRepository.findByUsername(userName);
        }
        catch (DataAccessException e) {
            String errorMessage = "Error occurred when retrieving user by their username from the database. " + e.getMessage();
            throw new RuntimeException(errorMessage);
        }
    }


    /**
     * Retrieves a user by their email.
     *
     * This method attempts to find a user in the database by their email. If an error occurs during the database access,
     * a {@link RuntimeException} is thrown with a descriptive error message.
     *
     * @param email The email of the user to be retrieved.
     * @return An {@link Optional} containing the user if found, or an empty {@link Optional} if no user with the given email exists.
     * @throws RuntimeException If an error occurs when accessing the database.
     */
    public Optional<User> findByEmail(String email) {
        try {
            return userRepository.findByEmail(email);
        }
        catch (DataAccessException e) {
            String errorMessage = "Error occurred when retrieving user by their email from the database. " + e.getMessage();
            throw new RuntimeException(errorMessage);
        }
    }

    @Transactional
    public void save(User user) {
        try {
            userRepository.save(user);
        }
        catch (DataAccessException e) {
            String errorMessage = "Error occurred when saving user to the database. " + e.getMessage();
            throw new RuntimeException(errorMessage);
        }
    }


    /**
     * Registers a new user by saving their details to the database.
     *
     * This method performs user registration by encoding the user's password using a {@link BCryptPasswordEncoder}
     * and then saving the user details to the database. If an error occurs during the database operation,
     * a {@link RuntimeException} is thrown with a descriptive error message.
     *
     * @param userRegistrationDTO The {@link UserRegistrationDTO} object containing the user's details, including the username, email, and password.
     *             The password is expected to be in plain text and will be encoded before saving.
     * @throws RuntimeException If an error occurs while saving the user to the database, such as a data access issue.
     */
    @Transactional
    public void register(UserRegistrationDTO userRegistrationDTO) {

        String username = userRegistrationDTO.getUsername();
        String email = userRegistrationDTO.getEmail();
        String password = userRegistrationDTO.getPassword();

        // if any of the fields in the http request are not provided or empty
        if (Stream.of(email, username, password).anyMatch(val -> val == null || val.trim().isEmpty())) {
            throw new CustomBadRequestException("User registration failed. Email, username, and password are all required and cannot be empty.");
        }

        // handle the case where an account already exists for this email
        if (findByEmail(email).isPresent()) {
            throw new EmailAlreadyRegisteredException("An account with this email is already registered.");
        }

        // handle the case where username already taken
        if (findByUsername(username).isPresent()) {
            throw new UsernameAlreadyRegisteredException("This username is already taken.");
        }

        User user = new User(username, email, password);

        // encode password and save user to db
        user.setPassword(encoder.encode(user.getPassword()));
        save(user);
    }

    @Transactional
    public void verify(String token, String username) {
        // validating token sent back from verification link
        if (!jwtService.isTokenValid(token, username)) {
            // remove user from db
            deleteByUsername(username);
            throw new InvalidTokenException("Verification failed due to invalid token. Try registering again.");
        }

        // updating verification status to verified
        Optional<User> possibleUser = findByUsername(username);
        if (possibleUser.isPresent()) {
            User user = possibleUser.get();
            user.setIsVerified(true);
            save(user);
        }
        else {
            throw new InvalidCredentialsException("Verification failed. User could not be found");
        }
    }


    /**
     * Deletes a user by their username.
     *
     * This method attempts to delete a user from the database by their username. If an error occurs during the database access,
     * a {@link RuntimeException} is thrown with a descriptive error message.
     *
     * @param username The username of the user to be deleted.
     * @throws RuntimeException If an error occurs when accessing the database.
     */
    @Transactional
    public void deleteByUsername(String username) {
        try {
            userRepository.deleteById(username);
        }
        catch (DataAccessException e) {
            String errorMessage = "Error occurred when deleting user from the database. " + e.getMessage();
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * Authenticates a user by validating their username and password.
     *
     * This method checks if a user with the specified username exists and is verified. It then compares the provided
     * password with the stored hashed password. If the user is not verified, an exception is thrown. If the username is not
     * found or the password does not match, an exception is also thrown.
     *
     * @param username The username of the user attempting to authenticate.
     * @param enteredPassword The plain text password provided by the user for authentication.
     * @return {@code true} if the username exists, the user is verified, and the password matches the stored hashed password;
     *         {@code false} otherwise.
     * @throws UserNotVerifiedException If the user is not verified.
     * @throws InvalidCredentialsException If the username does not exist or the password is incorrect.
     */
    public void authenticate(String username, String enteredPassword) {
        Optional<User> possibleUser = findByUsername(username);
        if (possibleUser.isPresent()) {
            User user = possibleUser.get();
            if (!user.isVerified()) {
                throw new UserNotVerifiedException("Authenticated failed. User not yet verified.");
            }

            String storedHashedPassword = user.getPassword();

            if (!encoder.matches(enteredPassword, storedHashedPassword)) {
                throw new InvalidCredentialsException("Authentication failed. Username or password incorrect.");
            }
        }
        else {
            throw new InvalidCredentialsException("Authentication failed. Username or password incorrect.");
        }
    }
}
