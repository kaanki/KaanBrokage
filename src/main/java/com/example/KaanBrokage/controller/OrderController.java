package com.example.KaanBrokage.controller;


import com.example.KaanBrokage.dto.*;
import com.example.KaanBrokage.entity.Order;
import com.example.KaanBrokage.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;


@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service){ this.service = service; }


    @PostMapping
    public CreateOrderResponse create(@Valid @RequestBody CreateOrderRequest req){
        Order o = service.create(req);
        CreateOrderResponse r = new CreateOrderResponse();
        r.id = o.getId();
        r.customerId = o.getCustomerId();
        r.assetName = o.getAssetName();
        r.side = o.getOrderSide().name();
        r.size = o.getSize();
        r.price = o.getPrice();
        r.status = o.getStatus();
        return r;
    }


    @GetMapping
    public PageResponse<OrderDto> list(@RequestParam String customerId,
                                       @RequestParam(required = false) LocalDate from,
                                       @RequestParam(required = false) LocalDate to,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size){
        Page<Order> p = service.list(customerId, from, to, page, size);
        return PageResponse.of(p.map(OrderDto::from));
    }


    @DeleteMapping("/{id}")
    public void cancel(@PathVariable Long id){
        service.cancel(id);
    }
}