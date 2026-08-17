package com.efashion.ecom.repository;

import com.efashion.ecom.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserIdOrderByOrderDateDesc(Integer userId);
}
