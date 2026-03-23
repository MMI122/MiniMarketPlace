package com.example.minimarketplaceprototype.service;

import com.example.minimarketplaceprototype.dto.UserRegistrationDto;
import com.example.minimarketplaceprototype.enums.RoleName;
import com.example.minimarketplaceprototype.model.Role;
import com.example.minimarketplaceprototype.model.User;
import com.example.minimarketplaceprototype.repository.RoleRepository;
import com.example.minimarketplaceprototype.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerUser(UserRegistrationDto registrationDto) {
        // Find the requested role (BUYER or SELLER) in the database
        RoleName roleName = RoleName.valueOf("ROLE_" + registrationDto.getRole().toUpperCase());
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Create the new user object
        User user = new User();
        user.setUsername(registrationDto.getUsername());

        // Encrypt the password before saving!
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole(role);

        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}