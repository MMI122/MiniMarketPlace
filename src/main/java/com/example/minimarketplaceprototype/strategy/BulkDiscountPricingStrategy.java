// have done strategy pattern here; just documenting it so that i can see it later.
package com.example.minimarketplaceprototype.strategy;

public class BulkDiscountPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(double unitPrice, int quantity) {
        double total = unitPrice * quantity;
        return total - (total * 0.10); // 10% discount for bulk orders
    }
}