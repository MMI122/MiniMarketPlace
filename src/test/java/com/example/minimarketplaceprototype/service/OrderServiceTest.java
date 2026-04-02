package com.example.minimarketplaceprototype.service;

import com.example.minimarketplaceprototype.model.Order;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("mubintestbuyer");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Laptop");
        testProduct.setPrice(BigDecimal.valueOf(1000.00));
        testProduct.setSeller(testUser);
    }

    @Test
    void test1_placeOrder_RegularStrategy() {
        when(userRepository.findByUsername("mubintestbuyer")).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Buying 4 uses Regular Pricing (4 * 1000 = 4000)
        orderService.placeOrder(1L, 4, "mubintestbuyer");

        verify(orderRepository, times(1)).save(argThat(order ->
                order.getTotalPrice().compareTo(BigDecimal.valueOf(4000.0)) == 0
        ));
    }

    @Test
    void test2_placeOrder_BulkDiscountStrategy() {
        when(userRepository.findByUsername("mubintestbuyer")).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Buying 5 triggers 10% Bulk Discount! (5000 - 10% = 4500)
        orderService.placeOrder(1L, 5, "mubintestbuyer");

        verify(orderRepository, times(1)).save(argThat(order ->
                order.getTotalPrice().compareTo(BigDecimal.valueOf(4500.0)) == 0
        ));
    }

    @Test
    void test3_placeOrder_UserNotFoundThrowsException() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.placeOrder(1L, 1, "ghost"));
    }

    @Test
    void test4_getOrdersByBuyer_Success() {
        when(userRepository.findByUsername("mubintestbuyer")).thenReturn(Optional.of(testUser));
        when(orderRepository.findByBuyerId(1L)).thenReturn(List.of(new Order()));

        List<Order> orders = orderService.getOrdersByBuyer("mubintestbuyer");
        assertEquals(1, orders.size());
    }

    @Test
    void test5_getOrdersBySeller_Success() {
        when(userRepository.findByUsername("seller1")).thenReturn(Optional.of(testUser));
        when(orderRepository.findByProductSellerId(1L)).thenReturn(List.of(new Order(), new Order()));

        List<Order> orders = orderService.getOrdersBySeller("seller1");
        assertEquals(2, orders.size());
    }
}