package com.nguyensao.attribute_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyensao.attribute_service.model.ProductOptionValue;

import java.util.List;

@Repository
public interface ProductOptionValueRepository extends JpaRepository<ProductOptionValue, Long> {

    List<ProductOptionValue> findByOptionId(Long optionId);
}