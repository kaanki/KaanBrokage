package com.example.KaanBrokage.service;

import com.example.KaanBrokage.dto.CreateOrderRequest;
import com.example.KaanBrokage.entity.*;
import com.example.KaanBrokage.exception.NotAllowedException;
import com.example.KaanBrokage.repository.AssetRepository;
import com.example.KaanBrokage.repository.OrderRepository;
import com.example.KaanBrokage.util.JwtUtil;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orders;
    private final AssetRepository assets;

    public OrderService(OrderRepository orders, AssetRepository assets) {
        this.orders = orders;
        this.assets = assets;
    }

    @Transactional
    public Order create(CreateOrderRequest req) {
        if (req.getSide() == Side.BUY && "TRY".equalsIgnoreCase(req.getAssetName()))
            throw new NotAllowedException("Cannot BUY TRY. TRY is only the quote asset.");

        Long customerId = JwtUtil.getCurrentCustomerId();
        if (customerId == null) {
            throw new IllegalStateException("Customer not authenticated");
        }
        String customerIdStr = customerId.toString();

        Order order = new Order();
        order.setCustomerId(customerIdStr);
        order.setAssetName(req.getAssetName());
        order.setOrderSide(req.getSide());
        order.setSize(req.getSize());
        order.setPrice(req.getPrice());
        order.setStatus(Status.PENDING);
        order.setCreateDate(Instant.now());


        if (req.getSide() == Side.BUY) {
            BigDecimal requiredTry = req.getSize().multiply(req.getPrice());
            Asset tryAsset = assets.findByCustomerIdAndAssetName(req.getCustomerId(), "TRY").orElseThrow(() -> new NotAllowedException("TRY asset not found for customer"));
            if (tryAsset.getUsableSize().compareTo(requiredTry) < 0)
                throw new NotAllowedException("Insufficient TRY usable balance");
            tryAsset.setUsableSize(tryAsset.getUsableSize().subtract(requiredTry));
            assets.save(tryAsset);
        } else {
            Asset asset = assets.findByCustomerIdAndAssetName(req.getCustomerId(), req.getAssetName()).orElseThrow(() -> new NotAllowedException("Asset not found for customer: " + req.getAssetName()));
            if (asset.getUsableSize().compareTo(req.getSize()) < 0)
                throw new NotAllowedException("Insufficient asset usable balance to SELL");
            asset.setUsableSize(asset.getUsableSize().subtract(req.getSize()));
            assets.save(asset);
        }

        return orders.save(order);
    }

    public Page<Order> list(String customerId, LocalDate from, LocalDate to, int page, int size) {
        PageRequest pr = PageRequest.of(page, size);
        if (from != null && to != null) {
            Instant f = from.atStartOfDay().toInstant(ZoneOffset.UTC);
            Instant t = to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).minusMillis(1);
            return orders.findByCustomerIdAndCreateDateBetween(customerId, f, t, pr);
        }
        return orders.findByCustomerId(customerId, pr);
    }

    public Page<Order> listMyOrders(LocalDate from, LocalDate to, int page, int size) {
        String customerId = String.valueOf(JwtUtil.getCurrentCustomerId());
        PageRequest pr = PageRequest.of(page, size);

        if (from != null && to != null) {
            Instant f = from.atStartOfDay().toInstant(ZoneOffset.UTC);
            Instant t = to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).minusMillis(1);
            return orders.findByCustomerIdAndCreateDateBetween(customerId, f, t, pr);
        }

        return orders.findByCustomerId(customerId, pr);
    }

    @Transactional
    public void cancel(Long orderId) {
        Order orderToCancel = orders.findById(orderId).orElseThrow(() -> new NotAllowedException("Order not found"));
        if (orderToCancel.getStatus() != Status.PENDING)
            throw new NotAllowedException("Only PENDING orders can be canceled");

        if (orderToCancel.getOrderSide() == Side.BUY) {
            BigDecimal refund = orderToCancel.getSize().multiply(orderToCancel.getPrice());
            Asset tryAsset = assets.findByCustomerIdAndAssetName(orderToCancel.getCustomerId(), "TRY").orElseThrow();
            tryAsset.setUsableSize(tryAsset.getUsableSize().add(refund));
            assets.save(tryAsset);
        } else {
            Asset asset = assets.findByCustomerIdAndAssetName(orderToCancel.getCustomerId(), orderToCancel.getAssetName()).orElseThrow();
            asset.setUsableSize(asset.getUsableSize().add(orderToCancel.getSize()));
            assets.save(asset);
        }
        orderToCancel.setStatus(Status.CANCELED);
        orders.save(orderToCancel);
    }

    @Transactional
    public void match(Long orderId) {
        Order orderToMatch = orders.findById(orderId).orElseThrow(() -> new NotAllowedException("Order not found"));
        if (orderToMatch.getStatus() != Status.PENDING)
            throw new NotAllowedException("Only PENDING orders can be matched");

        if (orderToMatch.getOrderSide() == Side.BUY) {
            BigDecimal cost = orderToMatch.getSize().multiply(orderToMatch.getPrice());
            Asset tryAsset = assets.findByCustomerIdAndAssetName(orderToMatch.getCustomerId(), "TRY").orElseThrow();
            if (tryAsset.getSize().compareTo(cost) < 0)
                throw new NotAllowedException("TRY total balance insufficient (inconsistent state)");
            tryAsset.setSize(tryAsset.getSize().subtract(cost));
            assets.save(tryAsset);

            Asset bought = assets.findByCustomerIdAndAssetName(orderToMatch.getCustomerId(), orderToMatch.getAssetName()).orElseGet(() -> {
                Asset asset = new Asset();
                asset.setCustomerId(orderToMatch.getCustomerId());
                asset.setAssetName(orderToMatch.getAssetName());
                asset.setSize(BigDecimal.ZERO);
                asset.setUsableSize(BigDecimal.ZERO);
                return assets.save(asset);
            });
            bought.setSize(bought.getSize().add(orderToMatch.getSize()));
            bought.setUsableSize(bought.getUsableSize().add(orderToMatch.getSize()));
            assets.save(bought);
        } else {
            Asset asset = assets.findByCustomerIdAndAssetName(orderToMatch.getCustomerId(), orderToMatch.getAssetName()).orElseThrow();
            if (asset.getSize().compareTo(orderToMatch.getSize()) < 0)
                throw new NotAllowedException("Asset total balance insufficient");
            asset.setSize(asset.getSize().subtract(orderToMatch.getSize()));
            assets.save(asset);

            BigDecimal proceeds = orderToMatch.getSize().multiply(orderToMatch.getPrice());
            Asset tryAsset = assets.findByCustomerIdAndAssetName(orderToMatch.getCustomerId(), "TRY").orElseThrow();
            tryAsset.setSize(tryAsset.getSize().add(proceeds));
            tryAsset.setUsableSize(tryAsset.getUsableSize().add(proceeds));
            assets.save(tryAsset);
        }

        orderToMatch.setStatus(Status.MATCHED);
        orders.save(orderToMatch);
    }


}
