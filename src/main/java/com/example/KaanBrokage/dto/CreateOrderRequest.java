package com.example.KaanBrokage.dto;


import com.example.KaanBrokage.entity.Side;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;


public class CreateOrderRequest {
    @NotBlank
    private String customerId;
    @NotBlank
    private String assetName;
    @NotNull
    private Side side;
    @NotNull
    @DecimalMin(value = "0.0001")
    private BigDecimal size;
    @NotNull
    @DecimalMin(value = "0.0001")
    private BigDecimal price;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public BigDecimal getSize() {
        return size;
    }

    public void setSize(BigDecimal size) {
        this.size = size;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}