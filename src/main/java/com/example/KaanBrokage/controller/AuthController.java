package com.example.KaanBrokage.controller;

import com.example.KaanBrokage.dto.LoginRequest;
import com.example.KaanBrokage.dto.LoginResponse;
import com.example.KaanBrokage.dto.RegisterRequest;
import com.example.KaanBrokage.entity.Customer;
import com.example.KaanBrokage.repository.CustomerRepository;
import com.example.KaanBrokage.service.CustomerService;
import com.example.KaanBrokage.util.JwtUtil;
import org.springframework.web.bind.annotation.*;
import com.example.KaanBrokage.dto.BaseResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final CustomerRepository customerRepository;
    private final CustomerService customerService;

    public AuthController(CustomerRepository customerRepository, CustomerService customerService) {
        this.customerRepository = customerRepository;
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public BaseResponse register(@RequestBody RegisterRequest request) {
        BaseResponse response = new BaseResponse(Boolean.TRUE,"register success");
        if (customerRepository.findByUsername(request.getUsername()) != null) {
            response.setMessage("Customer already exists");
            response.setSuccess(Boolean.FALSE);
            return response;
        }
        Customer c = new Customer();
        c.setUsername(request.getUsername());
        c.setPassword(request.getPassword());
        c.setRole(request.getRole());
        customerRepository.save(c);
        return response;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        Customer customer = customerService.authenticate(request.getUsername(), request.getPassword());
        String token = JwtUtil.generateToken(customer);
        return new LoginResponse(Boolean.TRUE,"Login Success",token, String.valueOf(customer.getId()), customer.getUsername());
    }
}
