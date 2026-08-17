package com.efashion.ecom.service;

import com.efashion.ecom.entity.CartItem;
import com.efashion.ecom.entity.Product;
import com.efashion.ecom.entity.User;
import com.efashion.ecom.repository.CartItemRepository;
import com.efashion.ecom.repository.ProductRepository;
import com.efashion.ecom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public List<CartItem> getCartItems(Integer userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public CartItem addToCart(Integer userId, Integer productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock (" + product.getStockQuantity() + ")");
        }

        Optional<CartItem> existingItemOpt = cartItemRepository.findByUserIdAndProductId(userId, productId);
        CartItem cartItem;
        if (existingItemOpt.isPresent()) {
            cartItem = existingItemOpt.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            if (product.getStockQuantity() < newQuantity) {
                throw new IllegalArgumentException("Total requested quantity exceeds available stock (" + product.getStockQuantity() + ")");
            }
            cartItem.setQuantity(newQuantity);
        } else {
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }

        return cartItemRepository.save(cartItem);
    }

    public CartItem updateCartItemQuantity(Integer userId, Integer cartItemId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!cartItem.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized action");
        }

        Product product = cartItem.getProduct();
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock (" + product.getStockQuantity() + ")");
        }

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    public void removeCartItem(Integer userId, Integer cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!cartItem.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized action");
        }

        cartItemRepository.delete(cartItem);
    }

    public void clearCart(Integer userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}
