package com.example.KaanBrokage.dto;


import com.example.KaanBrokage.entity.Status;

import java.math.BigDecimal;


public class CreateOrderResponse extends  BaseResponse{

    public CreateOrderResponse(boolean success, String message, Long id, String customerId, String assetName, String side, BigDecimal size, BigDecimal price, Status status) {
        super(success, message);
        this.id = id;
        this.customerId = customerId;
        this.assetName = assetName;
        this.side = side;
        this.size = size;
        this.price = price;
        this.status = status;
    }

    public Long id;
    public String customerId;
    public String assetName;
    public String side;
    public BigDecimal size;
    public BigDecimal price;
    public Status status;

}