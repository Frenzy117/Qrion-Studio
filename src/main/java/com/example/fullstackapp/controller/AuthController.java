package com.example.fullstackapp.controller;

import com.example.fullstackapp.model.User;
import com.example.fullstackapp.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") // For frontend connection
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return "User registered successfully: " + registeredUser.getUserName();
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> request) {
        String userName = request.get("userName");
        String password = request.get("password");

        return userService.authenticateUser(userName, password)
                .map(user -> "Login successful for: " + user.getUserName())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
    }
}
