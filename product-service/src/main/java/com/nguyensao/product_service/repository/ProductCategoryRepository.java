package com.nguyensao.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyensao.product_service.model.ProductCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    List<ProductCategory> findByIsPublishedTrue();

    Optional<ProductCategory> findBySlug(String slug);

    List<ProductCategory> findByParentId(Long parentId);
}