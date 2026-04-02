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
import com.example.minimarketplaceprototype.factory.UserFactory;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserFactory userFactory;

    @Override
    public void registerUser(UserRegistrationDto registrationDto) {
        // Find the requested role (BUYER or SELLER) in the database
        RoleName roleName = RoleName.valueOf("ROLE_" + registrationDto.getRole().toUpperCase());
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Encrypt the password before saving!
        String encodedPassword = passwordEncoder.encode(registrationDto.getPassword());

        //  THE FACTORY PATTERN IN ACTION
        // We pass the raw materials to the factory, and it hands us back a fully built User!
        User user = userFactory.createUser(registrationDto, role, encodedPassword);


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

    @Override
    public void toggleBanStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBanned(!user.isBanned()); // Flip it: if true make false, if false make true
        userRepository.save(user);
    }

    @Override
    public void sendAdminMessage(Long userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAdminMessage(message);
        userRepository.save(user);
    }

    @Override
    public void clearAdminMessage(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAdminMessage(null); // Wipe the message
        userRepository.save(user);  // Save the cleared state
    }
}