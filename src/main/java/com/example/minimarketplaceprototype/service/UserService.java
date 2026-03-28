package com.example.minimarketplaceprototype.service;

import com.example.minimarketplaceprototype.dto.UserRegistrationDto;
import com.example.minimarketplaceprototype.model.User;
import java.util.List;

public interface UserService {
    void registerUser(UserRegistrationDto registrationDto);
    User findByUsername(String username);
    List<User> findAll();
    void toggleBanStatus(Long userId);
    void sendAdminMessage(Long userId, String message);
    void clearAdminMessage(String username);
}