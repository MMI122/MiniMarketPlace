package com.example.minimarketplaceprototype.controller;

// Notice the updated import here!
import com.example.minimarketplaceprototype.dto.UserRegistrationDto;
import com.example.minimarketplaceprototype.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ApiAuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerApi(@RequestBody UserRegistrationDto userDto) {
        userService.registerUser(userDto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}