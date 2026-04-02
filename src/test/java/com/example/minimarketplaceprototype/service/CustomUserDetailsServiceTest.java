package com.example.minimarketplaceprototype.service;

import com.example.minimarketplaceprototype.enums.RoleName;
import com.example.minimarketplaceprototype.model.Role;
import com.example.minimarketplaceprototype.model.User;
import com.example.minimarketplaceprototype.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        Role buyerRole = new Role();
        buyerRole.setName(RoleName.ROLE_BUYER);

        testUser = new User();
        testUser.setUsername("secureuser");
        testUser.setPassword("hashedpass");
        testUser.setRole(buyerRole);
    }

    @Test
    void test9_loadUserByUsername_Success() {
        when(userRepository.findByUsername("secureuser")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("secureuser");

        assertNotNull(userDetails);
        assertEquals("secureuser", userDetails.getUsername());
        verify(userRepository, times(1)).findByUsername("secureuser");
    }

    @Test
    void test10_loadUserByUsername_UserNotFoundThrowsException() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("ghost");
        });
        verify(userRepository, times(1)).findByUsername("ghost");
    }
}