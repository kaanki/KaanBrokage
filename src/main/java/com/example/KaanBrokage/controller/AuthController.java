package com.example.KaanBrokage.controller;

import com.example.KaanBrokage.dto.BaseResponse;
import com.example.KaanBrokage.dto.LoginRequest;
import com.example.KaanBrokage.dto.LoginResponse;
import com.example.KaanBrokage.dto.RegisterRequest;
import com.example.KaanBrokage.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public BaseResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
