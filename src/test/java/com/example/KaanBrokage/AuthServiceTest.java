package com.example.KaanBrokage;

import com.example.KaanBrokage.dto.BaseResponse;
import com.example.KaanBrokage.dto.LoginRequest;
import com.example.KaanBrokage.dto.LoginResponse;
import com.example.KaanBrokage.dto.RegisterRequest;
import com.example.KaanBrokage.entity.Customer;
import com.example.KaanBrokage.entity.Role;
import com.example.KaanBrokage.repository.CustomerRepository;
import com.example.KaanBrokage.service.AuthService;
import com.example.KaanBrokage.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldReturnError_WhenUserAlreadyExists() {
        RegisterRequest request = new RegisterRequest("kaan", "1234", Role.CUSTOMER);
        when(customerRepository.findByUsername("kaan")).thenReturn(new Customer());

        BaseResponse response = authService.register(request);

        assertFalse(response.isSuccess());
        assertEquals("Customer already exists", response.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void register_ShouldSaveUser_WhenUserDoesNotExist() {

        RegisterRequest request = new RegisterRequest("kaan", "1234", Role.CUSTOMER);
        when(customerRepository.findByUsername("kaan")).thenReturn(null);

        BaseResponse response = authService.register(request);

        assertTrue(response.isSuccess());
        assertEquals("Register success", response.getMessage());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUsername("kaan");
        customer.setPassword("1234");
        customer.setRole(Role.CUSTOMER);

        LoginRequest request = new LoginRequest("kaan", "1234");

        when(customerService.authenticate("kaan", "1234")).thenReturn(customer);

        LoginResponse response = authService.login(request);

        assertTrue(response.isSuccess());
        assertEquals("Login Success", response.getMessage());
        assertNotNull(response.getToken());
        assertEquals("1", response.getCustomerId());
        assertEquals("kaan", response.getUsername());
    }
}
