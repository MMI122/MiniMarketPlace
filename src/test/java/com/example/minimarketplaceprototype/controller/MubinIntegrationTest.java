package com.example.minimarketplaceprototype.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional // Ensures the H2 database stays clean
class MubinIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // --- MUBIN'S INTEGRATION TEST 1: Authenticated Product Access ---
    @Test
    @WithMockUser(username = "testbuyer", roles = "BUYER")
    void test3_Integration_GetAllProductsEndpoint() throws Exception {
        // Act & Assert: An authenticated buyer can view the product list
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()); // Expect a JSON list of products
    }

    // --- MUBIN'S INTEGRATION TEST 2: Secure Endpoint Check ---
    @Test
    void test4_Integration_UnauthorizedAccessToOrders() throws Exception {
        // Act & Assert: Try to get orders WITHOUT the @WithMockUser annotation
        // Since we are not logged in, Spring Security will bounce us to the /login page (a 302 Redirect)
        mockMvc.perform(get("/api/orders/my-orders"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}