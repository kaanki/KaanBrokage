package com.example.KaanBrokage.repository;


import com.example.KaanBrokage.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;


public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findByCustomerIdAndAssetName(String customerId, String assetName);

    List<Asset> findByCustomerId(String customerId);
}