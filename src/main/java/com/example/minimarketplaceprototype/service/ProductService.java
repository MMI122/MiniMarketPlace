package com.example.minimarketplaceprototype.service;

import com.example.minimarketplaceprototype.dto.ProductDto;
import com.example.minimarketplaceprototype.model.Product;
import java.util.List;

public interface ProductService {
    void addProduct(ProductDto productDto, String sellerUsername);
    List<Product> getAllProducts();
    List<Product> getProductsBySeller(String username);
}