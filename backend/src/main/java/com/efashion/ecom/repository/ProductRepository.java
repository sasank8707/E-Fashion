package com.efashion.ecom.repository;

import com.efashion.ecom.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategoryId(Integer categoryId);
}
