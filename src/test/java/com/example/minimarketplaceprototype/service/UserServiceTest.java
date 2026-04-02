package com.example.minimarketplaceprototype.service;

import com.example.minimarketplaceprototype.dto.UserRegistrationDto;
import com.example.minimarketplaceprototype.enums.RoleName;
import com.example.minimarketplaceprototype.factory.UserFactory;
import com.example.minimarketplaceprototype.model.Role;
import com.example.minimarketplaceprototype.model.User;
import com.example.minimarketplaceprototype.repository.RoleRepository;
import com.example.minimarketplaceprototype.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserFactory userFactory;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationDto testDto;
    private Role testRole;
    private User testUser;

    @BeforeEach
    void setUp() {
        testDto = new UserRegistrationDto();
        testDto.setUsername("meheditest");
        testDto.setPassword("password123");
        testDto.setRole("BUYER");

        testRole = new Role();
        testRole.setName(RoleName.ROLE_BUYER);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("meheditest");
        testUser.setPassword("encoded_password");
        testUser.setRole(testRole);
        testUser.setBanned(false);
    }

    @Test
    void test1_registerUser_Success_UsesFactoryPattern() {
        when(roleRepository.findByName(RoleName.ROLE_BUYER)).thenReturn(Optional.of(testRole));
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userFactory.createUser(testDto, testRole, "encoded_password")).thenReturn(testUser);

        userService.registerUser(testDto);

        verify(userFactory, times(1)).createUser(testDto, testRole, "encoded_password");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void test2_registerUser_RoleNotFoundThrowsException() {
        when(roleRepository.findByName(RoleName.ROLE_BUYER)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                userService.registerUser(testDto)
        );
        assertEquals("Role not found", exception.getMessage());
    }

    @Test
    void test3_findByUsername_Success() {
        when(userRepository.findByUsername("meheditest")).thenReturn(Optional.of(testUser));
        User found = userService.findByUsername("meheditest");
        assertEquals("meheditest", found.getUsername());
    }

    @Test
    void test4_findByUsername_NotFoundThrowsException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.findByUsername("unknown"));
    }

    @Test
    void test5_findAll_ReturnsList() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        List<User> users = userService.findAll();
        assertEquals(1, users.size());
    }

    @Test
    void test6_toggleBanStatus_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        assertFalse(testUser.isBanned());

        userService.toggleBanStatus(1L);

        assertTrue(testUser.isBanned());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void test7_sendAdminMessage_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.sendAdminMessage(1L, "Warning: Behave!");

        assertEquals("Warning: Behave!", testUser.getAdminMessage());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void test8_clearAdminMessage_Success() {
        testUser.setAdminMessage("Old warning");
        when(userRepository.findByUsername("meheditest")).thenReturn(Optional.of(testUser));

        userService.clearAdminMessage("meheditest");

        assertNull(testUser.getAdminMessage());
        verify(userRepository, times(1)).save(testUser);
    }
}