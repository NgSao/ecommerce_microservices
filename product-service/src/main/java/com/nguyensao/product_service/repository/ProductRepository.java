package com.nguyensao.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nguyensao.product_service.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsPublishedTrue();

    Optional<Product> findBySku(String sku);

    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id = :categoryId")
    List<Product> findByCategory(Long categoryId);

    List<Product> findByBrandId(Long brandId);

    List<Product> findByNameContainingOrShortDescriptionContainingOrDescriptionContaining(
            String name, String shortDescription, String description);

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
}