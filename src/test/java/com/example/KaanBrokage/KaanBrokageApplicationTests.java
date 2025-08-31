package com.example.KaanBrokage;

import com.example.KaanBrokage.dto.CreateOrderRequest;
import com.example.KaanBrokage.entity.*;
import com.example.KaanBrokage.exception.NotAllowedException;
import com.example.KaanBrokage.repository.AssetRepository;
import com.example.KaanBrokage.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class KaanBrokageApplicationTests {

    @Autowired
    OrderService service;
    @Autowired
    AssetRepository assets;

    private static final String CustomerId = "1";

    @BeforeEach
    void ensureTry() {
        assets.findByCustomerIdAndAssetName(CustomerId, "TRY").ifPresentOrElse(a -> {
        }, () -> {
            Asset asset = new Asset();
            asset.setCustomerId(CustomerId);
            asset.setAssetName("TRY");
            asset.setSize(new BigDecimal("10000"));
            asset.setUsableSize(new BigDecimal("10000"));
            assets.save(asset);
        });
        Customer testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setUsername("1");
        testCustomer.setRole(Role.CUSTOMER);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                testCustomer.getId().toString(), null,
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void createBuyLocksTry() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerId(CustomerId);
        createOrderRequest.setAssetName("TTRK");
        createOrderRequest.setSide(Side.BUY);
        createOrderRequest.setSize(new BigDecimal("10"));
        createOrderRequest.setPrice(new BigDecimal("5"));

        Order order = service.create(createOrderRequest);
        assertEquals(Status.PENDING, order.getStatus());
        Asset tryAsset = assets.findByCustomerIdAndAssetName(CustomerId, "TRY").orElseThrow();
        assertEquals(new BigDecimal("99950.0000"), tryAsset.getUsableSize());
    }

    @Test
    void createSellLocksShares() {
        Asset TTRK = assets.findByCustomerIdAndAssetName(CustomerId, "TTRK").orElseGet(() -> {
            Asset asset = new Asset();
            asset.setCustomerId(CustomerId);
            asset.setAssetName("TTRK");
            asset.setSize(new BigDecimal("100"));
            asset.setUsableSize(new BigDecimal("100"));
            return assets.save(asset);
        });

        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerId(CustomerId);
        createOrderRequest.setAssetName("TTRK");
        createOrderRequest.setSide(Side.SELL);
        createOrderRequest.setSize(new BigDecimal("10"));
        createOrderRequest.setPrice(new BigDecimal("6"));

        Order order = service.create(createOrderRequest);
        assertEquals(Status.PENDING, order.getStatus());
        Asset after = assets.findByCustomerIdAndAssetName(CustomerId, "TTRK").orElseThrow();
        assertEquals(new BigDecimal("90"), after.getUsableSize());
    }

    @Test
    void cancelReleasesLocks() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerId(CustomerId);
        createOrderRequest.setAssetName("TTRK");
        createOrderRequest.setSide(Side.BUY);
        createOrderRequest.setSize(new BigDecimal("10"));
        createOrderRequest.setPrice(new BigDecimal("5"));
        Order order = service.create(createOrderRequest);
        service.cancel(order.getId());
        Asset tryAsset = assets.findByCustomerIdAndAssetName(CustomerId, "TRY").orElseThrow();
        assertEquals(new BigDecimal("100000.0000"), tryAsset.getUsableSize());
    }

    @Test
    void matchBuyMovesBalances() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerId(CustomerId);
        createOrderRequest.setAssetName("TTRK");
        createOrderRequest.setSide(Side.BUY);
        createOrderRequest.setSize(new BigDecimal("10"));
        createOrderRequest.setPrice(new BigDecimal("5"));
        Order o = service.create(createOrderRequest);
        service.match(o.getId());
        Asset tryAsset = assets.findByCustomerIdAndAssetName(CustomerId, "TRY").orElseThrow();
        assertEquals(new BigDecimal("99950.0000"), tryAsset.getSize());
    }

    @Test
    void insufficientFundsThrows() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerId(CustomerId);
        createOrderRequest.setAssetName("TTRK");
        createOrderRequest.setSide(Side.BUY);
        createOrderRequest.setSize(new BigDecimal("1000000"));
        createOrderRequest.setPrice(new BigDecimal("5"));
        assertThrows(NotAllowedException.class, () -> service.create(createOrderRequest));
    }
}
