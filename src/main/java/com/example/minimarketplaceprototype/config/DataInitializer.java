package com.example.minimarketplaceprototype.config;

import com.example.minimarketplaceprototype.enums.RoleName;
import com.example.minimarketplaceprototype.model.Role;
import com.example.minimarketplaceprototype.model.User;
import com.example.minimarketplaceprototype.repository.RoleRepository;
import com.example.minimarketplaceprototype.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Seed Roles
            if (roleRepository.count() == 0) {
                roleRepository.save(new Role(null, RoleName.ROLE_ADMIN));
                roleRepository.save(new Role(null, RoleName.ROLE_SELLER));
                roleRepository.save(new Role(null, RoleName.ROLE_BUYER));
            }

            // 2. Seed Admin User
            if (!userRepository.existsByUsername("admin")) {
                Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow();
                User admin = new User();
                admin.setUsername("admin");
                // The password is now safely encrypted using BCrypt!
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(adminRole);
                userRepository.save(admin);
            }
        };
    }
}