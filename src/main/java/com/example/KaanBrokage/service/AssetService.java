package com.example.KaanBrokage.service;


import com.example.KaanBrokage.entity.Asset;
import com.example.KaanBrokage.repository.AssetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;


@Service
public class AssetService {
    private final AssetRepository assets;
    public AssetService(AssetRepository assets){ this.assets = assets; }


    public List<Asset> listByCustomer(String customerId){
        return assets.findByCustomerId(customerId);
    }


    @Transactional
    public Asset getOrCreate(String customerId, String assetName){
        return assets.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseGet(() -> {
                    Asset a = new Asset();
                    a.setCustomerId(customerId);
                    a.setAssetName(assetName);
                    a.setSize(BigDecimal.ZERO);
                    a.setUsableSize(BigDecimal.ZERO);
                    return assets.save(a);
                });
    }


    @Transactional
    public Asset save(Asset a){ return assets.save(a); }
}