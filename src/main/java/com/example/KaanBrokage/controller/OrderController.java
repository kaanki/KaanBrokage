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

    public OrderController(OrderService service) {
        this.service = service;
    }


    @PostMapping
    public CreateOrderResponse create(@Valid @RequestBody CreateOrderRequest req) {
        Order order = service.create(req);
        CreateOrderResponse response = new CreateOrderResponse(
                Boolean.TRUE,
                "Success",
                order.getId(),
                order.getCustomerId(),
                order.getAssetName(),
                order.getOrderSide().name(),
                order.getSize(),
                order.getPrice(),
                order.getStatus()
        );
        return response;
    }


    @GetMapping
    public ListOrdersResponse<OrderDto> list(@RequestParam String customerId,
                                             @RequestParam(required = false) LocalDate startDate,
                                             @RequestParam(required = false) LocalDate endDate,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        Page<Order> p = service.list(customerId, startDate, endDate, page, size);
        return ListOrdersResponse.of(p.map(OrderDto::from));
    }


    @DeleteMapping("/{id}")
    public void cancel(@PathVariable Long id) {
        service.cancel(id);
    }
}