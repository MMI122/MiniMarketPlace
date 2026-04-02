package com.example.minimarketplaceprototype.controller;

import com.example.minimarketplaceprototype.dto.UserRegistrationDto;
import com.example.minimarketplaceprototype.enums.RoleName;
import com.example.minimarketplaceprototype.model.Role;
import com.example.minimarketplaceprototype.model.User;
import com.example.minimarketplaceprototype.repository.RoleRepository;
import com.example.minimarketplaceprototype.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional // Ensures the database wipes itself clean after every single test
class MehediIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @WithMockUser // <-- THE VIP PASS: Bypasses the 302 login redirect!
    void test1_Integration_RegisterUserEndpoint() throws Exception {
        // 1. Arrange: DataInitializer already added the Roles, so we just need the DTO!
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("new_integration_user");
        dto.setPassword("securepass");
        dto.setRole("BUYER");

        // 2. Act & Assert: Send POST request with CSRF token
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf()) // Bypasses the 403 Forbidden security block
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "testbuyer", roles = "BUYER")
    void test2_Integration_GetMyOrdersEndpoint() throws Exception {
        // 1. Arrange: Fetch the EXISTING role that DataInitializer created
        Role buyerRole = roleRepository.findByName(RoleName.ROLE_BUYER)
                .orElseThrow(() -> new RuntimeException("Role not found in DB!"));

        // Create the test user and assign the existing role
        User testBuyer = new User();
        testBuyer.setUsername("testbuyer");
        testBuyer.setPassword("hashedpassword");
        testBuyer.setRole(buyerRole);
        userRepository.save(testBuyer);

        // 2. Act & Assert: Send the GET request
        mockMvc.perform(get("/api/orders/my-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}