package com.example.KaanBrokage.dto;


import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;


public class ListOrdersRequest {
    public String customerId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    public LocalDate from;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    public LocalDate to;
    public Integer page = 0;
    public Integer size = 20;
}