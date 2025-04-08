package com.nguyensao.attribute_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyensao.attribute_service.model.ProductOptionCombination;

import java.util.List;

@Repository
public interface ProductOptionCombinationRepository extends JpaRepository<ProductOptionCombination, Long> {

    List<ProductOptionCombination> findByProductId(Long productId);

    void deleteByProductId(Long productId);
}