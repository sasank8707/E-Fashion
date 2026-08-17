package com.efashion.ecom.controller;

import com.efashion.ecom.entity.CartItem;
import com.efashion.ecom.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable Integer userId) {
        List<CartItem> items = cartService.getCartItems(userId);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<?> addToCart(
            @PathVariable Integer userId,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer productId = request.get("productId");
            Integer quantity = request.get("quantity");
            CartItem cartItem = cartService.addToCart(userId, productId, quantity);
            return new ResponseEntity<>(cartItem, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{userId}/update/{cartItemId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable Integer userId,
            @PathVariable Integer cartItemId,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer quantity = request.get("quantity");
            CartItem cartItem = cartService.updateCartItemQuantity(userId, cartItemId, quantity);
            return new ResponseEntity<>(cartItem, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{userId}/remove/{cartItemId}")
    public ResponseEntity<?> removeCartItem(
            @PathVariable Integer userId,
            @PathVariable Integer cartItemId) {
        try {
            cartService.removeCartItem(userId, cartItemId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Item removed from cart");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<?> clearCart(@PathVariable Integer userId) {
        try {
            cartService.clearCart(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cart cleared successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }
}
