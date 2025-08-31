package com.example.KaanBrokage.dto;

import lombok.Data;

@Data
public class LoginResponse extends BaseResponse {


    public LoginResponse(boolean success, String message, String token, String customerId, String username) {
        super(success, message);
        this.token = token;
        this.customerId = customerId;
        this.username = username;
    }

    private String token;
    private String customerId;
    private String username;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
