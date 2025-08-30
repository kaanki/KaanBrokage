package com.example.KaanBrokage.exception;


import java.time.Instant;


public class ApiError {
    public Instant timestamp = Instant.now();
    public int status;
    public String error;
    public String message;
}