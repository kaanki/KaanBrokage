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

    public OrderService(OrderRepository orders, AssetRepository assets){
        this.orders = orders; this.assets = assets;
    }

    @Transactional
    public Order create(CreateOrderRequest req){
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
            Asset tryAsset = assets.findByCustomerIdAndAssetName(req.getCustomerId(), "TRY")
                    .orElseThrow(() -> new NotAllowedException("TRY asset not found for customer"));
            if (tryAsset.getUsableSize().compareTo(requiredTry) < 0)
                throw new NotAllowedException("Insufficient TRY usable balance");
            tryAsset.setUsableSize(tryAsset.getUsableSize().subtract(requiredTry));
            assets.save(tryAsset);
        } else {
            Asset a = assets.findByCustomerIdAndAssetName(req.getCustomerId(), req.getAssetName())
                    .orElseThrow(() -> new NotAllowedException("Asset not found for customer: " + req.getAssetName()));
            if (a.getUsableSize().compareTo(req.getSize()) < 0)
                throw new NotAllowedException("Insufficient asset usable balance to SELL");
            a.setUsableSize(a.getUsableSize().subtract(req.getSize()));
            assets.save(a);
        }

        return orders.save(o);
    }

    public Page<Order> list(String customerId, LocalDate from, LocalDate to, int page, int size){
        PageRequest pr = PageRequest.of(page, size);
        if (from != null && to != null) {
            Instant f = from.atStartOfDay().toInstant(ZoneOffset.UTC);
            Instant t = to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).minusMillis(1);
            return orders.findByCustomerIdAndCreateDateBetween(customerId, f, t, pr);
        }
        return orders.findByCustomerId(customerId, pr);
    }

    @Transactional
    public void cancel(Long orderId){
        Order o = orders.findById(orderId).orElseThrow(() -> new NotAllowedException("Order not found"));
        if (o.getStatus() != Status.PENDING)
            throw new NotAllowedException("Only PENDING orders can be canceled");

        if (o.getOrderSide() == Side.BUY) {
            BigDecimal refund = o.getSize().multiply(o.getPrice());
            Asset tryAsset = assets.findByCustomerIdAndAssetName(o.getCustomerId(), "TRY")
                    .orElseThrow();
            tryAsset.setUsableSize(tryAsset.getUsableSize().add(refund));
            assets.save(tryAsset);
        } else {
            Asset a = assets.findByCustomerIdAndAssetName(o.getCustomerId(), o.getAssetName())
                    .orElseThrow();
            a.setUsableSize(a.getUsableSize().add(o.getSize()));
            assets.save(a);
        }
        o.setStatus(Status.CANCELED);
        orders.save(o);
    }

    @Transactional
    public void match(Long orderId){
        Order o = orders.findById(orderId).orElseThrow(() -> new NotAllowedException("Order not found"));
        if (o.getStatus() != Status.PENDING)
            throw new NotAllowedException("Only PENDING orders can be matched");

        if (o.getOrderSide() == Side.BUY) {
            BigDecimal cost = o.getSize().multiply(o.getPrice());
            Asset tryAsset = assets.findByCustomerIdAndAssetName(o.getCustomerId(), "TRY").orElseThrow();
            if (tryAsset.getSize().compareTo(cost) < 0)
                throw new NotAllowedException("TRY total balance insufficient (inconsistent state)");
            tryAsset.setSize(tryAsset.getSize().subtract(cost));
            assets.save(tryAsset);

            Asset bought = assets.findByCustomerIdAndAssetName(o.getCustomerId(), o.getAssetName())
                    .orElseGet(() -> {
                        Asset a = new Asset();
                        a.setCustomerId(o.getCustomerId());
                        a.setAssetName(o.getAssetName());
                        a.setSize(BigDecimal.ZERO);
                        a.setUsableSize(BigDecimal.ZERO);
                        return assets.save(a);
                    });
            bought.setSize(bought.getSize().add(o.getSize()));
            bought.setUsableSize(bought.getUsableSize().add(o.getSize()));
            assets.save(bought);
        } else {
            Asset asset = assets.findByCustomerIdAndAssetName(o.getCustomerId(), o.getAssetName()).orElseThrow();
            if (asset.getSize().compareTo(o.getSize()) < 0)
                throw new NotAllowedException("Asset total balance insufficient");
            asset.setSize(asset.getSize().subtract(o.getSize()));
            assets.save(asset);

            BigDecimal proceeds = o.getSize().multiply(o.getPrice());
            Asset tryAsset = assets.findByCustomerIdAndAssetName(o.getCustomerId(), "TRY").orElseThrow();
            tryAsset.setSize(tryAsset.getSize().add(proceeds));
            tryAsset.setUsableSize(tryAsset.getUsableSize().add(proceeds));
            assets.save(tryAsset);
        }

        o.setStatus(Status.MATCHED);
        orders.save(o);
    }
}
