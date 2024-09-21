package com.example.booktracker.user;

import com.example.booktracker.book.exception.CustomBadRequestException;
import com.example.booktracker.extra_services.JwtService;
import com.example.booktracker.user.exception.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public void verify(String username) {

        Optional<User> optionalUser = findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setIsVerified(true);
            save(user);
        }
        else {
            throw new UsernameNotFoundException("User is not registered in order to verify their account.");
        }

    }


    /**
     * Authenticates a user based on the provided login credentials.
     *
     * This method validates the provided username and password, retrieves the user from the database,
     * and checks if the user is verified and if the provided password matches the stored hashed password.
     * If any of the validation steps fail or if the user is not verified, appropriate exceptions are thrown.
     *
     * @param userLoginDTO The DTO containing the username and password for authentication.
     * @throws CustomBadRequestException if the username or password is null or empty.
     * @throws UserNotVerifiedException if the user has not been verified yet.
     * @throws InvalidCredentialsException if the username does not exist or the password is incorrect.
     */
    public void authenticate(UserLoginDTO userLoginDTO) {

        // validate the request's parameters
        String username = userLoginDTO.getUsername();
        String enteredPassword = userLoginDTO.getPassword();

        if (Stream.of(username, enteredPassword).anyMatch(val -> val == null || val.trim().isEmpty())) {
            throw new CustomBadRequestException("Authentication failed. Username and password are required values for authentication and cannot be empty.");
        }

        // get user from database and authenticate them
        Optional<User> possibleUser = findByUsername(username);
        if (possibleUser.isPresent()) {
            User user = possibleUser.get();
            if (!user.isVerified()) {
                throw new UserNotVerifiedException("Authentication failed. User not yet verified.");        // 401
            }

            String storedHashedPassword = user.getPassword();

            if (!encoder.matches(enteredPassword, storedHashedPassword)) {
                throw new InvalidCredentialsException("Username or password incorrect.");    // 401
            }
        }
        else {
            throw new InvalidCredentialsException("Username or password incorrect.");        // 401
        }
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
