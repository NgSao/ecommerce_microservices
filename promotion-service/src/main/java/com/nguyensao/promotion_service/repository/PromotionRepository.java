package com.nguyensao.promotion_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nguyensao.promotion_service.model.Promotion;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

}
