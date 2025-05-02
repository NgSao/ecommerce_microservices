package com.nguyensao.inventory_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nguyensao.inventory_service.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findBySkuVariant(String skuVariant);

    Optional<Inventory> findBySkuProductAndSkuVariantIsNull(String skuProduct);

    void deleteBySkuVariant(String skuVariant);

    void deleteBySkuProductAndSkuVariantIsNull(String skuProduct);

    List<Inventory> findAllByQuantityGreaterThan(Integer quantity);

    List<Inventory> findAllByQuantityEquals(Integer quantity);
}
