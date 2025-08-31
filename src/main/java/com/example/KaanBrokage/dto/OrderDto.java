package com.example.KaanBrokage.dto;


import com.example.KaanBrokage.entity.Order;
import com.example.KaanBrokage.entity.Status;

import java.math.BigDecimal;
import java.time.Instant;


public class OrderDto {
    public Long id;
    public String customerId;
    public String assetName;
    public String side;
    public BigDecimal size;
    public BigDecimal price;
    public Status status;
    public Instant createDate;


    public static OrderDto from(Order o) {
        OrderDto d = new OrderDto();
        d.customerId = o.getCustomerId();
        d.assetName = o.getAssetName();
        d.side = o.getOrderSide().name();
        d.size = o.getSize();
        d.price = o.getPrice();
        d.status = o.getStatus();
        d.createDate = o.getCreateDate();
        return d;
    }
}