package com.nguyensao.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyensao.product_service.model.ProductRelated;

import java.util.List;

@Repository
public interface ProductRelatedRepository extends JpaRepository<ProductRelated, Long> {

    List<ProductRelated> findByProductId(Long productId);

    void deleteByProductIdAndRelatedProductId(Long productId, Long relatedProductId);
}