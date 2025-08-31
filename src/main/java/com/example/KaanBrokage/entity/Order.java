package com.example.KaanBrokage.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;


@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String customerId;


    @Column(nullable = false)
    private String assetName;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Side orderSide;


    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal size;


    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal price;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;


    @Column(nullable = false)
    private Instant createDate = Instant.now();

    public Long getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAssetName() {
        return assetName;
    }

    public Side getOrderSide() {
        return orderSide;
    }

    public BigDecimal getSize() {
        return size;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public void setOrderSide(Side orderSide) {
        this.orderSide = orderSide;
    }

    public void setSize(BigDecimal size) {
        this.size = size;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }
}