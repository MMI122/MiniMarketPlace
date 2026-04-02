package com.example.minimarketplaceprototype.service;

import com.example.minimarketplaceprototype.dto.ProductDto;
import com.example.minimarketplaceprototype.model.Product;
import com.example.minimarketplaceprototype.model.User;
import com.example.minimarketplaceprototype.repository.OrderRepository;
import com.example.minimarketplaceprototype.repository.ProductRepository;
import com.example.minimarketplaceprototype.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private User testSeller;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        testSeller = new User();
        testSeller.setId(10L);
        testSeller.setUsername("seller1");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Smartphone");
        testProduct.setPrice(BigDecimal.valueOf(500.00));
        testProduct.setSeller(testSeller);

        productDto = new ProductDto();
        productDto.setName("Laptop");
        productDto.setDescription("Gaming laptop");
        productDto.setPrice(BigDecimal.valueOf(1200.00));
    }

    @Test
    void test6_getAllProducts_ReturnsList() {
        when(productRepository.findAll()).thenReturn(List.of(testProduct));
        List<Product> products = productService.getAllProducts();
        assertEquals(1, products.size());
    }

    @Test
    void test7_getProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        Product found = productService.getProductById(1L);
        assertEquals("Smartphone", found.getName());
    }

    @Test
    void test8_getProductById_NotFoundThrowsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productService.getProductById(99L));
    }

    @Test
    void test9_addProduct_Success() {
        when(userRepository.findByUsername("seller1")).thenReturn(Optional.of(testSeller));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        productService.addProduct(productDto, "seller1");

        verify(productRepository, times(1)).save(argThat(saved ->
                "Laptop".equals(saved.getName())
                        && "Gaming laptop".equals(saved.getDescription())
                        && BigDecimal.valueOf(1200.00).compareTo(saved.getPrice()) == 0
                        && saved.getSeller() == testSeller
        ));
    }

    @Test
    void test10_deleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.findByProductId(1L)).thenReturn(List.of());

        assertDoesNotThrow(() -> productService.deleteProduct(1L, "seller1"));

        verify(orderRepository, times(1)).deleteAll(List.of());
        verify(productRepository, times(1)).delete(testProduct);
    }
}