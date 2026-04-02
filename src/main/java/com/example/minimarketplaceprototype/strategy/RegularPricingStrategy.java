package com.example.minimarketplaceprototype.strategy;

public class RegularPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(double unitPrice, int quantity) {
        return unitPrice * quantity;
    }
}