package com.example.minimarketplaceprototype.strategy;

public interface PricingStrategy {
    double calculatePrice(double unitPrice, int quantity);
}