package com.example.booktracker.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/testing")
    public ResponseEntity<Map<String, String>> testDbConnection() {
        String userName = "zeyad";
        String email = "zeyame14@gmail.com";
        String password = "hashedpassword";

        User user = new User(userName, email, password);

        userService.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("Database connection", "success!");

        return ResponseEntity.ok(response);

    }
    @PostMapping("/user/register")
    public void registerUser(@RequestBody UserDTO userDTO) {

        String username = userDTO.getUsername();
        String email = userDTO.getEmail();
        String password = userDTO.getPassword();

        if (userService.findByEmail(email) != null) {
            // handle the case where an account already exists for this email
        }

        if (userService.findByUsername(username) != null) {
            // handle the case where username already taken
        }

        // register user


    }
}
