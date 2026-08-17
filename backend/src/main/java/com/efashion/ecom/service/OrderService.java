package com.efashion.ecom.service;

import com.efashion.ecom.entity.*;
import com.efashion.ecom.repository.OrderRepository;
import com.efashion.ecom.repository.ProductRepository;
import com.efashion.ecom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public Order placeOrder(Integer userId, String shippingAddress) {
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipping address is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<CartItem> cartItems = cartService.getCartItems(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot place order with an empty cart");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setStatus("PLACED");

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Product " + product.getName() + " is out of stock or does not have enough quantity");
            }

            // Deduct stock
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            // Create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice());

            orderItems.add(orderItem);

            // Add to total
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        // Save order and order items
        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cartService.clearCart(userId);

        return savedOrder;
    }

    public List<Order> getUserOrders(Integer userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }
}
