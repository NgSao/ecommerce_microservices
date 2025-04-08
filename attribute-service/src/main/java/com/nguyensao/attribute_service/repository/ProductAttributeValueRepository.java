package com.nguyensao.attribute_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyensao.attribute_service.model.ProductAttributeValue;

import java.util.List;

@Repository
public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, Long> {

    List<ProductAttributeValue> findByProductId(Long productId);

    List<ProductAttributeValue> findByAttributeId(Long attributeId);

    void deleteByProductId(Long productId);
}