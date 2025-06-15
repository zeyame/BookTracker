package com.example.booktracker.user.service;

import com.example.booktracker.book.exception.CustomBadRequestException;
import com.example.booktracker.user.dto.UserDTO;
import com.example.booktracker.user.exception.*;
import com.example.booktracker.user.model.User;
import com.example.booktracker.user.repository.UserRepository;
import com.example.booktracker.user.request.UserLoginRequest;
import com.example.booktracker.user.request.UserRegistrationRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public UserDTO getById(Long userId) {
        if (userId == null) {
            throw new CustomBadRequestException("User ID cannot be null");
        }
        return userRepository.findById(userId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new UserNotFoundException("User with id '" + userId + "' does not exist"));
    }


    public UserDTO getByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new CustomBadRequestException("Username cannot be null or empty.");
        }

        return userRepository.findByUsername(username)
                .map(this::mapToDTO)
                .orElseThrow(() -> new UserNotFoundException("Username '" + username + "' does not exist"));
    }

    /**
     * Retrieves a user by their username.
     *
     * This method attempts to find a user in the database by their username. If an error occurs during the database access,
     * a {@link RuntimeException} is thrown with a descriptive error message.
     *
     * @param username The username of the user to be retrieved.
     * @return An {@link UserDTO} if the user is found.
     * @throws UserNotFoundException if the user is not found
     * @throws RuntimeException If an error occurs when accessing the database.
     */
    public UserDTO findByUsername(String username) {
        try {
            return userRepository.findByUsername(username)
                    .map(this::mapToDTO)
                    .orElseThrow(() -> new UserNotFoundException("No user found with username " + username));
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
    public UserDTO findByEmail(String email) {
        try {
            return userRepository.findByEmail(email)
                    .map(this::mapToDTO)
                    .orElseThrow(() -> new UserNotFoundException("No user found with email: " + email));
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
     * @param username The id of the user to be deleted.
     * @throws RuntimeException If an error occurs when accessing the database.
     */
    @Transactional
    public void deleteByUsername(String username) {
        try {
            userRepository.deleteByUsername(username);
        }
        catch (DataAccessException e) {
            String errorMessage = "Error occurred when deleting user from the database. " + e.getMessage();
            throw new RuntimeException(errorMessage);
        }
    }


    /**
     * Validates the user registration data, ensuring both username and email are provided
     * and checking if the user already exists in the system.
     *
     * This method performs the following validations:
     * <ul>
     *     <li>Both username and email must not be null or empty. If either is null or empty,
     *     a {@link CustomBadRequestException} is thrown.</li>
     *     <li>It checks if an account with the provided email already exists. If so, an
     *     {@link EmailAlreadyRegisteredException} is thrown.</li>
     *     <li>It checks if the provided username is already taken. If so, a
     *     {@link UsernameAlreadyRegisteredException} is thrown.</li>
     * </ul>
     *
     * @param userRegistrationDTO the {@link UserRegistrationRequest} object containing the user's
     *                            registration data such as username and email
     * @throws CustomBadRequestException if either the username or email is null or empty
     * @throws EmailAlreadyRegisteredException if an account with the provided email already exists
     * @throws UsernameAlreadyRegisteredException if the provided username is already taken
     */
    public void validate (UserRegistrationRequest userRegistrationDTO) {
        String username = userRegistrationDTO.getUsername();
        String email = userRegistrationDTO.getEmail();

        if (Stream.of(username, email).anyMatch(val -> val == null || val.trim().isEmpty())) {
            throw new CustomBadRequestException("Email and username are both required to ensure user does not already exist.");
        }

        assertEmailNotAlreadyRegistered(email);

        assertUsernameNotTaken(username);
    }

    /**
     * Registers a new user by saving their details to the database.
     *
     * This method performs user registration by encoding the user's password using a {@link BCryptPasswordEncoder}
     * and then saving the user details to the database. If an error occurs during the database operation,
     * a {@link RuntimeException} is thrown with a descriptive error message.
     *
     * @param userRegistrationDTO The {@link UserRegistrationRequest} object containing the user's details, including the username, email, and password.
     *             The password is expected to be in plain text and will be encoded before saving.
     * @throws RuntimeException If an error occurs while saving the user to the database, such as a data access issue.
     */
    @Transactional
    public void register(UserRegistrationRequest userRegistrationDTO) {

        String username = userRegistrationDTO.getUsername();
        String email = userRegistrationDTO.getEmail();
        String password = userRegistrationDTO.getPassword();

        // if any of the fields in the http request are not provided or empty
        if (Stream.of(email, username, password).anyMatch(val -> val == null || val.trim().isEmpty())) {
            throw new CustomBadRequestException("User registration failed. Email, username, and password are all required and cannot be empty.");
        }

        // handle the case where an account already exists for this email
        assertEmailNotAlreadyRegistered(email);

        // handle the case where username already taken
        assertUsernameNotTaken(username);

        User user = new User(username, email, password);

        // encode password and save user to db
        user.setPassword(encoder.encode(user.getPassword()));
        save(user);
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
    public void authenticate(UserLoginRequest userLoginDTO) {

        // validate the request's parameters
        String username = userLoginDTO.getUsername();
        String enteredPassword = userLoginDTO.getPassword();

        if (Stream.of(username, enteredPassword).anyMatch(val -> val == null || val.trim().isEmpty())) {
            throw new CustomBadRequestException("Authentication failed. Username and password are required values for authentication and cannot be empty.");
        }

        // get user from database and authenticate them
        Optional<User> possibleUser = userRepository.findByUsername(username);
        if (possibleUser.isPresent()) {
            User user = possibleUser.get();
            String storedHashedPassword = user.getPassword();

            if (!encoder.matches(enteredPassword, storedHashedPassword)) {
                throw new InvalidCredentialsException("Username or password incorrect.");    // 401
            }

            // user passed authentication
        }
        else {
            throw new InvalidCredentialsException("Username or password incorrect.");        // 401
        }
    }

    public List<User> findAll() {
        try {
            return userRepository.findAll();
        }
        catch (DataAccessException e) {
            String errorMessage = "Error occurred when retrieving all users. " + e.getMessage();
            throw new RuntimeException(errorMessage);
        }
    }


    public void assertUsernameNotTaken(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyRegisteredException("This username is already taken");
        }
    }

    public void assertEmailNotAlreadyRegistered(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyRegisteredException("An account with this email is already registered.");
        }
    }

    private UserDTO mapToDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
    }
}
