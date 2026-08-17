package com.efashion.ecom.controller;

import com.efashion.ecom.entity.Product;
import com.efashion.ecom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(required = false) Integer categoryId) {
        List<Product> products = productService.getAllProducts(categoryId);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        try {
            Product product = productService.getProductById(id);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        try {
            Product newProduct = productService.addProduct(product);
            return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Integer id, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        try {
            productService.deleteProduct(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }
}
