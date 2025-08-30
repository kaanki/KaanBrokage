package com.example.KaanBrokage.controller;


import com.example.KaanBrokage.service.OrderService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
    private final OrderService service;
    public AdminOrderController(OrderService service){ this.service = service; }

    @PostMapping("/{id}/match")
    public void match(@PathVariable Long id){
        service.match(id);
    }
}
