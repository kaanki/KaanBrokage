package com.example.KaanBrokage.dto;


import com.example.KaanBrokage.entity.Status;
import java.math.BigDecimal;


public class CreateOrderResponse {
    public Long id;
    public String customerId;
    public String assetName;
    public String side;
    public BigDecimal size;
    public BigDecimal price;
    public Status status;
}