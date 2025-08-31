package com.example.KaanBrokage.service;

import com.example.KaanBrokage.dto.BaseResponse;
import com.example.KaanBrokage.dto.LoginRequest;
import com.example.KaanBrokage.dto.LoginResponse;
import com.example.KaanBrokage.dto.RegisterRequest;
import com.example.KaanBrokage.entity.Customer;
import com.example.KaanBrokage.repository.CustomerRepository;
import com.example.KaanBrokage.util.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;
    private final CustomerService customerService;

    public AuthService(CustomerRepository customerRepository, CustomerService customerService) {
        this.customerRepository = customerRepository;
        this.customerService = customerService;
    }

    public BaseResponse register(RegisterRequest request) {
        if (customerRepository.findByUsername(request.getUsername()) != null) {
            return new BaseResponse(Boolean.FALSE, "Customer already exists");
        }

        Customer c = new Customer();
        c.setUsername(request.getUsername());
        c.setPassword(request.getPassword());
        c.setRole(request.getRole());
        customerRepository.save(c);

        return new BaseResponse(Boolean.TRUE, "Register success");
    }

    public LoginResponse login(LoginRequest request) {
        Customer customer = customerService.authenticate(request.getUsername(), request.getPassword());
        String token = JwtUtil.generateToken(customer);
        return new LoginResponse(
                Boolean.TRUE,
                "Login Success",
                token,
                String.valueOf(customer.getId()),
                customer.getUsername()
        );
    }
}
