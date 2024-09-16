package com.example.booktracker.user;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
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


    /**
     * Saves a user to the database.
     *
     * This method encodes the user's password and then attempts to save the user to the database. If an error occurs during the
     * database access, a {@link RuntimeException} is thrown with a descriptive error message
     *
     * @param user The user to be saved.
     * @throws RuntimeException If an error occurs when accessing the database.
     */
    @Transactional
    public void save(User user) {
        // encode password
        user.setPassword(encoder.encode(user.getPassword()));
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

}
