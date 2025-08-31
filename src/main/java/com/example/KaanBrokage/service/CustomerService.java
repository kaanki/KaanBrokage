package com.example.KaanBrokage.service;

import com.example.KaanBrokage.entity.Customer;
import com.example.KaanBrokage.repository.CustomerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerService implements UserDetailsService {

    private final com.example.KaanBrokage.repository.CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByUsername(username);
        if (customer == null) {
            new UsernameNotFoundException("User not found");
        }
        return User.withUsername(customer.getUsername())
                .password(customer.getPassword())
                .roles(customer.getRole().name())
                .build();
    }

    public Customer authenticate(String username, String password) {
        Customer customer = customerRepository.findByUsername(username);
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        if (!customer.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        return customer;
    }
}
