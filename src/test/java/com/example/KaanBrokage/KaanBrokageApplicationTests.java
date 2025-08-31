package com.example.KaanBrokage;

import com.example.KaanBrokage.dto.CreateOrderRequest;
import com.example.KaanBrokage.entity.*;
import com.example.KaanBrokage.exception.NotAllowedException;
import com.example.KaanBrokage.repository.AssetRepository;
import com.example.KaanBrokage.repository.OrderRepository;
import com.example.KaanBrokage.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class KaanBrokageApplicationTests {

    @Autowired OrderService service;
    @Autowired AssetRepository assets;
    @Autowired OrderRepository orders;

    private static final String C = "CUST-1";

    @BeforeEach
    void ensureTry(){
        assets.findByCustomerIdAndAssetName(C, "TRY").ifPresentOrElse(a -> {}, () -> {
            Asset a = new Asset();
            a.setCustomerId(C);
            a.setAssetName("TRY");
            a.setSize(new BigDecimal("10000"));
            a.setUsableSize(new BigDecimal("10000"));
            assets.save(a);
        });
    }

//    @Test
//    void createBuyLocksTry(){
//        CreateOrderRequest r = new CreateOrderRequest();
//        r.setCustomerId(C);
//        r.setAssetName("TTRK");
//        r.setSide(Side.BUY);
//        r.setSize(new BigDecimal("10"));
//        r.setPrice(new BigDecimal("5"));
//
//        Order o = service.create(r);
//        assertEquals(Status.PENDING, o.getStatus());
//        Asset tryAsset = assets.findByCustomerIdAndAssetName(C, "TRY").orElseThrow();
//        assertEquals(new BigDecimal("99950.0000"), tryAsset.getUsableSize());
//    }
//
//    @Test
//    void createSellLocksShares(){
//        Asset TTRK = assets.findByCustomerIdAndAssetName(C, "TTRK").orElseGet(() -> {
//            Asset a = new Asset();
//            a.setCustomerId(C);
//            a.setAssetName("TTRK");
//            a.setSize(new BigDecimal("100"));
//            a.setUsableSize(new BigDecimal("100"));
//            return assets.save(a);
//        });
//
//        CreateOrderRequest r = new CreateOrderRequest();
//        r.setCustomerId(C);
//        r.setAssetName("TTRK");
//        r.setSide(Side.SELL);
//        r.setSize(new BigDecimal("10"));
//        r.setPrice(new BigDecimal("6"));
//
//        Order o = service.create(r);
//        assertEquals(Status.PENDING, o.getStatus());
//        Asset after = assets.findByCustomerIdAndAssetName(C, "TTRK").orElseThrow();
//        assertEquals(new BigDecimal("90"), after.getUsableSize());
//    }
//
//    @Test
//    void cancelReleasesLocks(){
//        CreateOrderRequest r = new CreateOrderRequest();
//        r.setCustomerId(C);
//        r.setAssetName("TTRK");
//        r.setSide(Side.BUY);
//        r.setSize(new BigDecimal("10"));
//        r.setPrice(new BigDecimal("5"));
//        Order o = service.create(r);
//        service.cancel(o.getId());
//        Asset tryAsset = assets.findByCustomerIdAndAssetName(C, "TRY").orElseThrow();
//        assertEquals(new BigDecimal("100000.0000"), tryAsset.getUsableSize());
//    }
//
//    @Test
//    void matchBuyMovesBalances(){
//        CreateOrderRequest r = new CreateOrderRequest();
//        r.setCustomerId(C);
//        r.setAssetName("TTRK");
//        r.setSide(Side.BUY);
//        r.setSize(new BigDecimal("10"));
//        r.setPrice(new BigDecimal("5"));
//        Order o = service.create(r);
//        service.match(o.getId());
//        Asset tryAsset = assets.findByCustomerIdAndAssetName(C, "TRY").orElseThrow();
//        assertEquals(new BigDecimal("99950.0000"), tryAsset.getSize());
//    }
//
//    @Test
//    void insufficientFundsThrows(){
//        CreateOrderRequest r = new CreateOrderRequest();
//        r.setCustomerId(C);
//        r.setAssetName("TTRK");
//        r.setSide(Side.BUY);
//        r.setSize(new BigDecimal("1000000"));
//        r.setPrice(new BigDecimal("5"));
//        assertThrows(NotAllowedException.class, () -> service.create(r));
//    }
}
