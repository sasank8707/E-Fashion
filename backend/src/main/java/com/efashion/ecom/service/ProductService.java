package com.efashion.ecom.service;

import com.efashion.ecom.entity.Product;
import com.efashion.ecom.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts(Integer categoryId) {
        if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId);
        }
        return productRepository.findAll();
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + id + " not found"));
    }

    public Product addProduct(Product product) {
        validateProduct(product);
        return productRepository.save(product);
    }

    public Product updateProduct(Integer id, Product productDetails) {
        Product product = getProductById(id);
        
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());
        product.setCategory(productDetails.getCategory());
        product.setImageUrl(productDetails.getImageUrl());

        validateProduct(product);
        return productRepository.save(product);
    }

    public void deleteProduct(Integer id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
        if (product.getStockQuantity() == null || product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Product stock quantity cannot be negative");
        }
    }
}
