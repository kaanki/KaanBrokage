package com.example.KaanBrokage.controller;


import com.example.KaanBrokage.entity.Asset;
import com.example.KaanBrokage.service.AssetService;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/assets")
public class AssetController {
    private final AssetService assets;
    public AssetController(AssetService assets){ this.assets = assets; }


    @GetMapping
    public List<Asset> list(@RequestParam String customerId){
        return assets.listByCustomer(customerId);
    }
}