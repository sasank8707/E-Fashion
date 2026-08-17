package com.efashion.ecom.controller;

import com.efashion.ecom.entity.Order;
import com.efashion.ecom.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place/{userId}")
    public ResponseEntity<?> placeOrder(
            @PathVariable Integer userId,
            @RequestBody Map<String, String> request) {
        try {
            String shippingAddress = request.get("shippingAddress");
            Order order = orderService.placeOrder(userId, shippingAddress);
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserOrders(@PathVariable Integer userId) {
        try {
            List<Order> orders = orderService.getUserOrders(userId);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }
}
