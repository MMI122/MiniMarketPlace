package com.example.minimarketplaceprototype.factory;

import com.example.minimarketplaceprototype.dto.UserRegistrationDto;
import com.example.minimarketplaceprototype.model.Role;
import com.example.minimarketplaceprototype.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    // The Factory Method
    public User createUser(UserRegistrationDto dto, Role role, String encodedPassword) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(encodedPassword);
        user.setRole(role);
        return user;
    }
}