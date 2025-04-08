package com.nguyensao.attribute_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyensao.attribute_service.model.ProductAttributeTemplate;

@Repository
public interface ProductAttributeTemplateRepository extends JpaRepository<ProductAttributeTemplate, Long> {
}