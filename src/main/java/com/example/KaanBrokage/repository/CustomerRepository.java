package com.example.KaanBrokage.repository;

import com.example.KaanBrokage.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByUsername(String username);
}