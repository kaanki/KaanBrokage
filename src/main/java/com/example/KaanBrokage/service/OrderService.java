package com.example.KaanBrokage.service;

import com.example.KaanBrokage.dto.CreateOrderRequest;
import com.example.KaanBrokage.entity.*;
import com.example.KaanBrokage.exception.NotAllowedException;
import com.example.KaanBrokage.repository.AssetRepository;
import com.example.KaanBrokage.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

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

        Order o = new Order();
        o.setCustomerId(req.getCustomerId());
        o.setAssetName(req.getAssetName());
        o.setOrderSide(req.getSide());
        o.setSize(req.getSize());
        o.setPrice(req.getPrice());
        o.setStatus(Status.PENDING);
        o.setCreateDate(Instant.now());


        if (req.getSide() == Side.BUY) {
            BigDecimal requiredTry = req.getSize().multiply(req.getPrice());
            Asset tryAsset = assets.findByCustomerIdAndAssetName(req.getCustomerId(), "TRY").orElseThrow(() -> new NotAllowedException("TRY asset not found for customer"));
            if (tryAsset.getUsableSize().compareTo(requiredTry) < 0)
                throw new NotAllowedException("Insufficient TRY usable balance");
            tryAsset.setUsableSize(tryAsset.getUsableSize().subtract(requiredTry));
            assets.save(tryAsset);
        } else {
            Asset a = assets.findByCustomerIdAndAssetName(req.getCustomerId(), req.getAssetName()).orElseThrow(() -> new NotAllowedException("Asset not found for customer: " + req.getAssetName()));
            if (a.getUsableSize().compareTo(req.getSize()) < 0)
                throw new NotAllowedException("Insufficient asset usable balance to SELL");
            a.setUsableSize(a.getUsableSize().subtract(req.getSize()));
            assets.save(a);
        }

        return orders.save(o);
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

    @Transactional
    public void cancel(Long orderId) {
        Order orderToCancel = orders.findById(orderId).orElseThrow(() -> new NotAllowedException("Order not found"));
        if (orderToCancel.getStatus() != Status.PENDING) throw new NotAllowedException("Only PENDING orders can be canceled");

        if (orderToCancel.getOrderSide() == Side.BUY) {
            BigDecimal refund = orderToCancel.getSize().multiply(orderToCancel.getPrice());
            Asset tryAsset = assets.findByCustomerIdAndAssetName(orderToCancel.getCustomerId(), "TRY").orElseThrow();
            tryAsset.setUsableSize(tryAsset.getUsableSize().add(refund));
            assets.save(tryAsset);
        } else {
            Asset a = assets.findByCustomerIdAndAssetName(orderToCancel.getCustomerId(), orderToCancel.getAssetName()).orElseThrow();
            a.setUsableSize(a.getUsableSize().add(orderToCancel.getSize()));
            assets.save(a);
        }
        orderToCancel.setStatus(Status.CANCELED);
        orders.save(orderToCancel);
    }

    @Transactional
    public void match(Long orderId) {
        Order orderToMatch = orders.findById(orderId).orElseThrow(() -> new NotAllowedException("Order not found"));
        if (orderToMatch.getStatus() != Status.PENDING) throw new NotAllowedException("Only PENDING orders can be matched");

        if (orderToMatch.getOrderSide() == Side.BUY) {
            BigDecimal cost = orderToMatch.getSize().multiply(orderToMatch.getPrice());
            Asset tryAsset = assets.findByCustomerIdAndAssetName(orderToMatch.getCustomerId(), "TRY").orElseThrow();
            if (tryAsset.getSize().compareTo(cost) < 0)
                throw new NotAllowedException("TRY total balance insufficient (inconsistent state)");
            tryAsset.setSize(tryAsset.getSize().subtract(cost));
            assets.save(tryAsset);

            Asset bought = assets.findByCustomerIdAndAssetName(orderToMatch.getCustomerId(), orderToMatch.getAssetName()).orElseGet(() -> {
                Asset a = new Asset();
                a.setCustomerId(orderToMatch.getCustomerId());
                a.setAssetName(orderToMatch.getAssetName());
                a.setSize(BigDecimal.ZERO);
                a.setUsableSize(BigDecimal.ZERO);
                return assets.save(a);
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
