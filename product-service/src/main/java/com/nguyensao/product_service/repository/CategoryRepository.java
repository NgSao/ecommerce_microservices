package com.nguyensao.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyensao.product_service.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

}
