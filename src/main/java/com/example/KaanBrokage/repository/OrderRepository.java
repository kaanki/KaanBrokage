package com.example.KaanBrokage.repository;


import com.example.KaanBrokage.entity.Order;
import com.example.KaanBrokage.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.Instant;
import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByCustomerIdAndCreateDateBetween(String customerId, Instant from, Instant to, Pageable pageable);
    Page<Order> findByCustomerId(String customerId, Pageable pageable);
    List<Order> findByCustomerId(String customerId);
    long countByCustomerIdAndStatus(String customerId, Status status);
}