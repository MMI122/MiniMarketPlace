package com.example.minimarketplaceprototype.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String username;
    private String password;
    private String role; // Will receive "SELLER" or "BUYER" from the HTML registration form
}