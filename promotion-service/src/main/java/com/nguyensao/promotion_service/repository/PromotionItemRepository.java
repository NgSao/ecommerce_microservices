package com.nguyensao.promotion_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nguyensao.promotion_service.model.PromotionItem;

public interface PromotionItemRepository extends JpaRepository<PromotionItem, Long> {

}
